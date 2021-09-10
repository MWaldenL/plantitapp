package com.mobdeve.s15.group8.mobdeve_mp.controller.services

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
        loadImageLocal(plant.filePath, context, imageView)
    }

    fun loadImageLocal(filename: String, context: Context, imageView: ImageView) {
        Glide.with(context)
            .load("file:$filename")
            .apply(RequestOptions().override(300, 300).centerCrop())
            .into(imageView)
    }
}