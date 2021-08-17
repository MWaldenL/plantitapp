package com.mobdeve.s15.group8.mobdeve_mp.controller.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.ViewSinglePlantActivity
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders.PlantViewHolder

class PlantListAdapter(
    private val data: ArrayList<Plant>,
    private val viewPlantLauncher: ActivityResultLauncher<Intent>):
    RecyclerView.Adapter<PlantViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_plant, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.bindData(data[position])
        holder.itemView.setOnClickListener {
            val PLANT_KEY = holder.itemView.context.getString(R.string.PLANT_KEY)
            val plantIntent = Intent(holder.itemView.context, ViewSinglePlantActivity::class.java)
            plantIntent.putExtra(PLANT_KEY, data[position])
            viewPlantLauncher.launch(plantIntent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}