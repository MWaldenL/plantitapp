package com.mobdeve.s15.group8.mobdeve_mp.controller.services

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import java.io.File

object ImageLoadingService {
    fun loadImage(plant: Plant, context: Context, imageView: ImageView) {
        if (plant.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(plant.imageUrl)
                .placeholder(R.drawable.bg_img_temp)
                .into(imageView)
            return
        }
        val imgFile = File(plant.filePath)
        val bmp = BitmapFactory.decodeFile(imgFile.absolutePath)
        imageView.setImageBitmap(bmp)
//
//        if (plant.filePath == "") { // load the image from cloud
//            Glide.with(context)
//                .load(plant.imageUrl)
//                .placeholder(R.drawable.bg_img_temp)
//                .into(imageView)
//        } else { // load the image from app storage
//            val imgFile = File(plant.filePath)
//            val bmp = BitmapFactory.decodeFile(imgFile.absolutePath)
//            imageView.setImageBitmap(bmp)
//        }
    }
}