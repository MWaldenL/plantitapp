package com.mobdeve.s15.group8.mobdeve_mp.controller.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.viewing.ViewSinglePlantActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders.PlantGridViewHolder
import com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders.PlantListViewHolder
import com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders.PlantViewHolder
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.singletons.LayoutType

class PlantListAdapter(private val data: ArrayList<Plant>): RecyclerView.Adapter<PlantViewHolder>() {
    var viewType: Int = LayoutType.LINEAR_VIEW.ordinal

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return if (viewType == LayoutType.LINEAR_VIEW.ordinal)
            R.layout.item_plant_list else
            R.layout.item_plant_grid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(viewType, parent, false)
        return if (viewType == R.layout.item_plant_grid)
            PlantGridViewHolder(view) else
            PlantListViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.bindData(data[position])
        holder.itemView.setOnClickListener {
            val PLANT_KEY = holder.itemView.context.getString(R.string.PLANT_KEY)
            val plantIntent = Intent(holder.itemView.context, ViewSinglePlantActivity::class.java)
            plantIntent.putExtra(PLANT_KEY, data[position])
            holder.itemView.context.startActivity(plantIntent)
        }
    }

    override fun getItemCount(): Int = data.size
}