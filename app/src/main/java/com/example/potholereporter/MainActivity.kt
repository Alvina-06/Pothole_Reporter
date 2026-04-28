package com.example.potholereporter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri


class MainActivity : AppCompatActivity() {

    lateinit var etLocation: EditText
    lateinit var etDescription: EditText
    lateinit var btnAdd: Button
    lateinit var listView: ListView

    val reports = ArrayList<String>()
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etLocation = findViewById(R.id.etLocation)
        etDescription = findViewById(R.id.etDescription)
        btnAdd = findViewById(R.id.btnAdd)
        listView = findViewById(R.id.listView)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, reports)
        listView.adapter = adapter

        btnAdd.setOnClickListener {

            val location = etLocation.text.toString()
            val desc = etDescription.text.toString()

            if (location.isNotEmpty() && desc.isNotEmpty()) {

                // dummy coordinates (safe approach)
                val lat = 12.9716
                val lon = 77.5946

                val mapLink = "https://maps.google.com/?q=$lat,$lon"

                val report = "Pothole Report\n📍 $location\n🛠 $desc\n🔗 $mapLink"


                reports.add(report)
                adapter.notifyDataSetChanged()

                etLocation.text.clear()
                etDescription.text.clear()
            } else {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Open map when clicked
        listView.setOnItemClickListener { _, _, position, _ ->
            val item = reports[position]
            val url = item.substringAfter("https")

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https$url"))

            // 👇 THIS LINE IS THE CHANGE
            startActivity(Intent.createChooser(intent, "Open with"))
        }
    }
}