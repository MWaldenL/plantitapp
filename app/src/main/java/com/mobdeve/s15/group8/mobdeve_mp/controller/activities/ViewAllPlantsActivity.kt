package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.PlantListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

// Can be converted to fragment later on for tabbed interface
class ViewAllPlantsActivity: AppCompatActivity(), CoroutineScope {
    private lateinit var recyclerView: RecyclerView
    private val mViewPlantLauncher = registerForActivityResult(StartActivityForResult()) { result -> }
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.IO + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_plants)
        launch {
            PlantRepository.fetchData()
            withContext(Dispatchers.Main) {
                recyclerView = findViewById(R.id.recyclerview_plant)
                recyclerView.adapter = PlantListAdapter(PlantRepository.plantList, mViewPlantLauncher)
                recyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
            }
        }
    }
}