package com.example.smsforwarder

import android.content.Context
import android.content.SharedPreferences

class SafePrefs(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences("safe_sms_prefs", Context.MODE_PRIVATE)

    var forwardTo: String
        get() = pref.getString("forwardTo", "") ?: ""
        set(v) = pref.edit().putString("forwardTo", v).apply()

    var allowedList: List<String>
        get() = pref.getStringSet("allowed", emptySet())?.toList() ?: emptyList()
        set(v) = pref.edit().putStringSet("allowed", v.toSet()).apply()

    fun addAllowed(num: String) {
        val set = pref.getStringSet("allowed", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        set.add(normalize(num))
        pref.edit().putStringSet("allowed", set).apply()
    }

    fun removeAllowed(num: String) {
        val set = pref.getStringSet("allowed", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        set.remove(normalize(num))
        pref.edit().putStringSet("allowed", set).apply()
    }

    fun isAllowed(num: String): Boolean {
        val set = pref.getStringSet("allowed", emptySet()) ?: emptySet()
        return set.contains(normalize(num))
    }

    private fun normalize(n: String): String {
        val digits = n.filter { it.isDigit() || it == '+' }
        return when {
            digits.startsWith("+98") -> "0" + digits.removePrefix("+98")
            else -> digits
        }
    }
}
