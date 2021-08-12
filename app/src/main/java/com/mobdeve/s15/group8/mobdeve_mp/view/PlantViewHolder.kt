package com.mobdeve.s15.group8.mobdeve_mp.view

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.Plant

class PlantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val mImage: ImageView = itemView.findViewById(R.id.image_plant)
    fun bindData(plant: Plant) {
        mImage.setImageResource(R.drawable.ic_launcher_background)
    }
}