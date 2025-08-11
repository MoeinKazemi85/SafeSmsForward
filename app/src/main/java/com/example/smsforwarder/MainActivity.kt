package com.example.smsforwarder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS = arrayOf(
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_CONTACTS
    )
    private val REQ_PERM = 101

    private lateinit var prefs: SafePrefs
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = SafePrefs(this)

        // request runtime permissions
        requestNeededPermissions()

        val editTarget = findViewById<EditText>(R.id.editTarget)
        val btnSaveTarget = findViewById<Button>(R.id.btnSaveTarget)
        val editAllowed = findViewById<EditText>(R.id.editAllowed)
        val btnAddAllowed = findViewById<Button>(R.id.btnAddAllowed)
        listView = findViewById(R.id.listAllowed)

        editTarget.setText(prefs.forwardTo)

        btnSaveTarget.setOnClickListener {
            prefs.forwardTo = editTarget.text.toString().trim()
            Toast.makeText(this, "شماره مقصد ذخیره شد", Toast.LENGTH_SHORT).show()
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, prefs.allowedList.toMutableList())
        listView.adapter = adapter

        btnAddAllowed.setOnClickListener {
            val num = editAllowed.text.toString().trim()
            if (num.isNotEmpty()) {
                prefs.addAllowed(num)
                adapter.clear()
                adapter.addAll(prefs.allowedList)
                adapter.notifyDataSetChanged()
                editAllowed.text.clear()
            }
        }

        listView.setOnItemLongClickListener { _, _, pos, _ ->
            val item = adapter.getItem(pos)!!
            prefs.removeAllowed(item)
            adapter.remove(item)
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "حذف شد", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun requestNeededPermissions() {
        val toRequest = PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (toRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, toRequest.toTypedArray(), REQ_PERM)
        }
    }
}
