package com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders

import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import java.io.File

class PlantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val mImage: ImageView = itemView.findViewById(R.id.image_plant)
    fun bindData(plant: Plant) {
        if (plant.filePath == "") { // load the image from cloud
            Glide.with(itemView.context)
                 .load(plant.imageUrl)
                 .placeholder(R.drawable.ic_launcher_background)
                 .into(mImage)
        } else { // load the image from app storage
            val imgFile = File(plant.filePath)
            val bmp = BitmapFactory.decodeFile(imgFile.absolutePath)
            mImage.setImageBitmap(bmp)
        }
    }
}