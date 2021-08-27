package com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant

abstract class PlantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract fun bindData(plant: Plant)
}