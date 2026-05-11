package com.example.potholereporter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class ReportAdapter(
    val ctx: Context,   // ✅ renamed from context → ctx
    val list: ArrayList<Report>
) : ArrayAdapter<Report>(ctx, 0, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = LayoutInflater.from(ctx)
            .inflate(R.layout.list_item, parent, false)

        val item = list[position]

        val tvLocation = view.findViewById<TextView>(R.id.tvLocation)
        val tvDesc = view.findViewById<TextView>(R.id.tvDesc)
        val img = view.findViewById<ImageView>(R.id.imgPothole)

        tvLocation.text = "📍 ${item.location}"
        tvDesc.text = "🛠 ${item.description}"

        try {
            val fileInput = ctx.openFileInput(item.imageUri)
            val bitmap = BitmapFactory.decodeStream(fileInput)
            img.setImageBitmap(bitmap)
        } catch (e: Exception) {
            img.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        // ✨ Animation for list item
        view.alpha = 0f
        view.animate().alpha(1f).setDuration(400).start()

        return view
    }
}

