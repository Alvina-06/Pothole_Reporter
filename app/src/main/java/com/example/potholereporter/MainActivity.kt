package com.example.potholereporter

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var etLocation: EditText
    lateinit var etDescription: EditText
    lateinit var btnAdd: Button
    lateinit var listView: ListView
    lateinit var btnCamera: Button
    lateinit var imageView: ImageView

    lateinit var db: DatabaseHelper

    var imageUri: String = ""

    val reports = ArrayList<Report>()
    lateinit var adapter: ReportAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // CAMERA PERMISSION
        if (checkSelfPermission(android.Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
        }

        db = DatabaseHelper(this)

        etLocation = findViewById(R.id.etLocation)
        etDescription = findViewById(R.id.etDescription)
        btnAdd = findViewById(R.id.btnAdd)
        listView = findViewById(R.id.listView)
        btnCamera = findViewById(R.id.btnCamera)
        imageView = findViewById(R.id.imageView)

        adapter = ReportAdapter(this, reports)
        listView.adapter = adapter

        // Load DB data
        reports.addAll(db.getAllData())
        adapter.notifyDataSetChanged()

        // CAMERA
        btnCamera.setOnClickListener {

            btnCamera.animate()
                .rotation(360f)
                .setDuration(400)
                .start()

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 1)
        }

        // ADD REPORT
        btnAdd.setOnClickListener {
            btnAdd.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction {
                    btnAdd.animate().scaleX(1f).scaleY(1f).setDuration(100)
                }
            val location = etLocation.text.toString()
            val desc = etDescription.text.toString()
            if (location.isNotEmpty() && desc.isNotEmpty()) {

                val success = db.insertData(location, desc, imageUri)
                if (success) {
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()

                    reports.clear()
                    reports.addAll(db.getAllData())
                    adapter.notifyDataSetChanged()

                    etLocation.text.clear()
                    etDescription.text.clear()

                } else {
                    Toast.makeText(this, "Error saving", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // MAP OPEN
        listView.setOnItemClickListener { _, _, position, _ ->
            val item = reports[position]

            val mapLink =
                "https://www.google.com/maps/search/?api=1&query=${item.location}"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapLink))
            startActivity(Intent.createChooser(intent, "Open with"))
        }

        // DELETE
        listView.setOnItemLongClickListener { _, _, position, _ ->

            val item = reports[position]

            val deleted = db.deleteData(item.location)

            if (deleted) {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()

                reports.clear()
                reports.addAll(db.getAllData())
                adapter.notifyDataSetChanged()
            }

            true
        }
    }
    //CAMERA RESULT
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {

            val bitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(bitmap)

            val filename = "pothole_" + System.currentTimeMillis() + ".png"
            val file = openFileOutput(filename, MODE_PRIVATE)

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, file)
            file.close()

            imageUri = filename
        }
    }
}

//package com.example.potholereporter
//
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.provider.MediaStore
//import android.widget.*
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//
//class MainActivity : AppCompatActivity() {
//
//    lateinit var etLocation: EditText
//    lateinit var etDescription: EditText
//    lateinit var btnAdd: Button
//    lateinit var listView: ListView
//    lateinit var btnCamera: Button
//    lateinit var imageView: ImageView
//
//    lateinit var db: DatabaseHelper
//
//    var imageUri: String = ""
//
//    val reports = ArrayList<String>()
//    lateinit var adapter: ArrayAdapter<String>
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // ✅ CAMERA PERMISSION
//        if (checkSelfPermission(android.Manifest.permission.CAMERA) !=
//            PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
//        }
//
//        // Initialize DB
//        db = DatabaseHelper(this)
//
//        // Link UI
//        etLocation = findViewById(R.id.etLocation)
//        etDescription = findViewById(R.id.etDescription)
//        btnAdd = findViewById(R.id.btnAdd)
//        listView = findViewById(R.id.listView)
//        btnCamera = findViewById(R.id.btnCamera)
//        imageView = findViewById(R.id.imageView)
//
//        // Adapter setup
//        adapter = object : ArrayAdapter<String>(
//            this,
//            android.R.layout.simple_list_item_1,
//            reports
//        ) {
//            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
//                val view = super.getView(position, convertView, parent)
//
//                view.alpha = 0f
//                view.animate().alpha(1f).setDuration(500).start()
//
//                return view
//            }
//        }
//        listView.adapter = adapter
//
//        // Load existing DB data
//        reports.addAll(db.getAllData())
//        adapter.notifyDataSetChanged()
//
//        // 📸 CAMERA BUTTON
//        btnCamera.setOnClickListener {
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(intent, 1)
//        }
//
//        // ➕ ADD BUTTON
//        btnAdd.setOnClickListener {
//
//            btnAdd.animate()
//                .scaleX(0.85f)
//                .scaleY(0.85f)
//                .setDuration(100)
//                .withEndAction {
//                    btnAdd.animate().scaleX(1f).scaleY(1f).duration = 100
//                }
//
//            val location = etLocation.text.toString()
//            val desc = etDescription.text.toString()
//
//            if (location.isNotEmpty() && desc.isNotEmpty()) {
//
//                val success = db.insertData(location, desc, imageUri)
//
//                if (success) {
//
//                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
//
//                    // Reload data from DB
//                    reports.clear()
//                    reports.addAll(db.getAllData())
//                    adapter.notifyDataSetChanged()
//
//                    etLocation.text.clear()
//                    etDescription.text.clear()
//
//                } else {
//                    Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show()
//                }
//
//            } else {
//                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // 🌍 OPEN MAP
//        listView.setOnItemClickListener { _, _, position, _ ->
//            val item = reports[position]
//
//            // extract location from text
//            val location = item.substringAfter("📍 ").substringBefore("\n")
//
//            val mapLink = "https://www.google.com/maps/search/?api=1&query=$location"
//
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapLink))
//            startActivity(Intent.createChooser(intent, "Open with"))
//        }
//
//
//        listView.setOnItemLongClickListener { _, _, position, _ ->
//
//            val item = reports[position]
//
//            // extract location from text
//            val location = item.substringAfter("📍 ").substringBefore("\n")
//
//            val deleted = db.deleteData(location)
//
//            if (deleted) {
//                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
//
//                // reload list
//                reports.clear()
//                reports.addAll(db.getAllData())
//                adapter.notifyDataSetChanged()
//
//            } else {
//                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
//            }
//
//            true
//        }
//
//    }
//
//    // 📸 CAMERA RESULT
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//
//            val bitmap = data?.extras?.get("data") as Bitmap
//            imageView.setImageBitmap(bitmap)
//
//            // SAVE IMAGE TO INTERNAL STORAGE
//            val filename = "pothole_" + System.currentTimeMillis() + ".png"
//            val file = openFileOutput(filename, MODE_PRIVATE)
//
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, file)
//            file.close()
//
//            // SAVE FILE NAME AS URI
//            imageUri = filename
//        }
//    }
//}