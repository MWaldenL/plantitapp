package com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders

import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import java.io.File
import android.graphics.ColorMatrixColorFilter
import android.graphics.ColorMatrix
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.ImageLoadingService

class PlantGridViewHolder(itemView: View): PlantViewHolder(itemView) {
    private val ivImage: ImageView = itemView.findViewById(R.id.iv_plant_grid)
    private val tvPlantGridHeader: TextView = itemView.findViewById(R.id.tv_plant_grid_name)
    private val tvPlantGridSubheader: TextView = itemView.findViewById(R.id.tv_plant_grid_name2)

    override fun bindData(plant: Plant) {
        ImageLoadingService.loadImage(plant, itemView.context, ivImage)
        if (plant.death) {
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)
            ivImage.colorFilter = ColorMatrixColorFilter(matrix)
        }

        if (plant.nickname == "") {
            tvPlantGridHeader.text = plant.name
            tvPlantGridSubheader.visibility = View.INVISIBLE
        } else {
            tvPlantGridHeader.text = plant.nickname
            tvPlantGridSubheader.text = plant.name
            tvPlantGridSubheader.visibility = View.VISIBLE
        }
    }
}