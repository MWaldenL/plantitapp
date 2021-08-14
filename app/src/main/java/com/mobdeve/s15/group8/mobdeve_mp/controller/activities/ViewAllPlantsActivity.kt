package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.PlantListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.model.PlantRepository
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
        val pr = PlantRepository()
        Log.d("DEBUG", "Created view all plants")
        launch {
            pr.getData()
            withContext(Dispatchers.Main) {
                Log.d("DEBUG", "after get Data")
                recyclerView = findViewById(R.id.recyclerview_plant)
                recyclerView.adapter = PlantListAdapter(pr.plantList, mViewPlantLauncher)
                recyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
            }
        }
    }
}