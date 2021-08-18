package com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders

import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import java.io.File

class PlantListViewHolder(itemView: View): PlantViewHolder(itemView) {
    private val mImagePlant: ImageView = itemView.findViewById(R.id.iv_list_plant)
    private val mTvNick: TextView = itemView.findViewById(R.id.tv_list_plant_nick)
    private val mTvName: TextView = itemView.findViewById(R.id.tv_list_plant_name)

    override fun bindData(plant: Plant) {
        mLoadImage(plant)
        mTvNick.text = plant.nickname
        mTvName.text = plant.name
    }

    private fun mLoadImage(plant: Plant) {
        if (plant.filePath == "") { // load the image from cloud
            Glide.with(itemView.context)
                .load(plant.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(mImagePlant)
        } else { // load the image from app storage
            val imgFile = File(plant.filePath)
            val bmp = BitmapFactory.decodeFile(imgFile.absolutePath)
            mImagePlant.setImageBitmap(bmp)
        }
    }
}