package com.example.potholereporter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri

class MainActivity : AppCompatActivity() {

    lateinit var etLocation: EditText
    lateinit var etDescription: EditText
    lateinit var btnAdd: Button
    lateinit var listView: ListView
    lateinit var sharedPref: SharedPreferences

    val reports = ArrayList<String>()
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SharedPreferences
        sharedPref = getSharedPreferences("PotholeData", MODE_PRIVATE)

        // Link UI elements
        etLocation = findViewById(R.id.etLocation)
        etDescription = findViewById(R.id.etDescription)
        btnAdd = findViewById(R.id.btnAdd)
        listView = findViewById(R.id.listView)

        // Setup adapter FIRST
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, reports)
        listView.adapter = adapter

        // Load saved data AFTER adapter setup
        val savedData = sharedPref.getString("reports", "")
        if (!savedData.isNullOrEmpty()) {
            val list = savedData.split("||")
            reports.addAll(list)
            adapter.notifyDataSetChanged()
        }

        // Button click → add report
        btnAdd.setOnClickListener {

            val location = etLocation.text.toString()
            val desc = etDescription.text.toString()

            if (location.isNotEmpty() && desc.isNotEmpty()) {

                val lat = 12.9719
                val lon = 77.5937

                val mapLink = "https://maps.google.com/?q=$lat,$lon"

                val report = "Pothole Report\n📍 $location\n🛠 $desc\n🔗 $mapLink"

                // Add to list
                reports.add(report)

                // Save to SharedPreferences
                val data = reports.joinToString("||")
                sharedPref.edit().putString("reports", data).apply()

                // Refresh UI
                adapter.notifyDataSetChanged()

                // Clear inputs
                etLocation.text.clear()
                etDescription.text.clear()

            } else {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Click item → open location
        listView.setOnItemClickListener { _, _, position, _ ->
            val item = reports[position]
            val url = item.substringAfter("https")

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https$url"))
            startActivity(Intent.createChooser(intent, "Open with"))
        }
    }
}