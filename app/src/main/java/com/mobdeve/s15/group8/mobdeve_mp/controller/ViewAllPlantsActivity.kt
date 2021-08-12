package com.mobdeve.s15.group8.mobdeve_mp.controller

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.PlantDataHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

// Can be converted to fragment later on for tabbed interface
class ViewAllPlantsActivity: AppCompatActivity(), CoroutineScope {
    private lateinit var recyclerView: RecyclerView
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_plants)
        launch {
            val data = PlantDataHelper().getData()
            withContext(Dispatchers.Main) {
                recyclerView = findViewById(R.id.recyclerview_plant)
                recyclerView.adapter = PlantListAdapter(data)
                recyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
            }
        }
    }
}