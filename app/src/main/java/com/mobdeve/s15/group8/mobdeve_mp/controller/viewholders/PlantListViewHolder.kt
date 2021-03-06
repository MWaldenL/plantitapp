package com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.ImageLoadingService
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant

class PlantListViewHolder(itemView: View): PlantViewHolder(itemView) {
    private val mImagePlant: ImageView = itemView.findViewById(R.id.iv_list_plant)
    private val mTvNick: TextView = itemView.findViewById(R.id.tv_list_plant_nick)
    private val mTvName: TextView = itemView.findViewById(R.id.tv_list_plant_name)

    override fun bindData(plant: Plant) {
        ImageLoadingService.loadImage(plant.filePath, plant.imageUrl, itemView.context, mImagePlant)
        if (plant.nickname == "") {
            mTvNick.text = plant.name
            mTvName.visibility = View.GONE
        } else {
            mTvNick.text = plant.nickname
            mTvName.text = plant.name
            mTvName.visibility = View.VISIBLE
        }

        if (plant.death) {
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)
            mImagePlant.colorFilter = ColorMatrixColorFilter(matrix)
        }
    }
}