package com.mobdeve.s15.group8.mobdeve_mp.controller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.PlantDataHelper

class ViewAllPlantsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_plants)
        val data = PlantDataHelper().fetchData()
        recyclerView = findViewById(R.id.recyclerview_plant)
        recyclerView.adapter = PlantListAdapter(data)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }
}