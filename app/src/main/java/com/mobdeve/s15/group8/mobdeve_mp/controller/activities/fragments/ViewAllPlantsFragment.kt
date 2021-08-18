package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.PlantListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.NewPlantCallback
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.RefreshCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.NewPlantInstance
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository

// Can be converted to fragment later on for tabbed interface
class ViewAllPlantsFragment: Fragment(), NewPlantCallback, RefreshCallback {
    private lateinit var recyclerViewAlive: RecyclerView
    private lateinit var recyclerViewDead: RecyclerView
    private lateinit var swipeToRefreshLayout: SwipeRefreshLayout

    private var mAlive = arrayListOf<Plant>()
    private var mDead = arrayListOf<Plant>()

    private val mViewPlantLauncher = registerForActivityResult(StartActivityForResult()) { result -> }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_all_plants, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NewPlantInstance.setOnNewPlantListener(this)
        PlantRepository.setOnDataFetchedListener(this)

        swipeToRefreshLayout = view.findViewById(R.id.sr_layout_view_all_plants)
        swipeToRefreshLayout.setOnRefreshListener { PlantRepository.getData() }

        recyclerViewAlive = view.findViewById(R.id.recyclerview_plant)
        recyclerViewAlive.adapter = PlantListAdapter(mAlive, mViewPlantLauncher)
        recyclerViewAlive.layoutManager = GridLayoutManager(requireContext(), 2)

        recyclerViewDead = view.findViewById(R.id.recyclerview_dead)
        recyclerViewDead.adapter = PlantListAdapter(mDead, mViewPlantLauncher)
        recyclerViewDead.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    override fun onStart() {
        super.onStart()

        mDead.clear()
        mAlive.clear()

        for (plant in PlantRepository.plantList) {
            if (plant.death)
                mDead.add(plant)
            else
                mAlive.add(plant)   
        }

        updateView()
    }

    override fun updateView() {
        recyclerViewAlive.adapter?.notifyDataSetChanged()
        recyclerViewDead.adapter?.notifyDataSetChanged()
    }

    override fun onDataFetched() {
        swipeToRefreshLayout.isRefreshing = false
        Log.d("HATDOG", swipeToRefreshLayout.isRefreshing.toString())

    }
}