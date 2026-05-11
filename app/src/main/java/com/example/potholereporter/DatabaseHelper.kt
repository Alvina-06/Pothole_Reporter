package com.example.potholereporter

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "PotholeDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE reports(id INTEGER PRIMARY KEY AUTOINCREMENT, location TEXT, description TEXT, imageUri TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS reports")
        onCreate(db)
    }

    fun insertData(location: String, description: String, imageUri: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("location", location)
        values.put("description", description)
        values.put("imageUri", imageUri)
        val result = db.insert("reports", null, values)
        return result != -1L
    }

    fun getAllData(): List<Report> {
        val list = ArrayList<Report>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM reports", null)

        while (cursor.moveToNext()) {
            val location = cursor.getString(1)
            val desc = cursor.getString(2)
            val imageUri = cursor.getString(3)

            list.add(Report(location, desc, imageUri))
        }

        cursor.close()
        return list
    }

    fun deleteData(location: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete("reports", "location=?", arrayOf(location))
        return result > 0
    }
}