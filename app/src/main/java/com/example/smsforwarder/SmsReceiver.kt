package com.example.smsforwarder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.telephony.SmsManager
import android.util.Log

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.provider.Telephony.SMS_RECEIVED") return

        val bundle: Bundle? = intent.extras
        val prefs = SafePrefs(context)

        val pdus = bundle?.get("pdus") as? Array<*>
        if (pdus == null) return

        val smsList = mutableListOf<SmsMessage>()
        for (p in pdus) {
            val format = bundle.getString("format")
            val sms = SmsMessage.createFromPdu(p as ByteArray, format)
            smsList.add(sms)
        }

        for (sms in smsList) {
            val origin = sms.originatingAddress ?: continue
            val body = sms.messageBody ?: ""
            val norm = normalizeNumber(origin)

            if (prefs.isAllowed(norm)) {
                val target = prefs.forwardTo
                if (target.isNotBlank()) {
                    try {
                        SmsManager.getDefault().sendTextMessage(target, null, "From: $origin\n$body", null, null)
                        Log.d("SmsReceiver", "Forwarded sms from $origin to $target")
                    } catch (e: Exception) {
                        Log.e("SmsReceiver", "Failed to forward: ${e.message}")
                    }
                }
            } else {
                Log.d("SmsReceiver", "Ignored sms from $origin")
            }
        }
    }

    private fun normalizeNumber(n: String): String {
        val digits = n.filter { it.isDigit() || it == '+' }
        return when {
            digits.startsWith("+98") -> "0" + digits.removePrefix("+98")
            else -> digits
        }
    }
}
