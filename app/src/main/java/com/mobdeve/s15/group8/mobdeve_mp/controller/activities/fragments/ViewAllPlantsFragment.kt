package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.PlantListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository

// Can be converted to fragment later on for tabbed interface
class ViewAllPlantsFragment: Fragment() {
    private lateinit var recyclerView: RecyclerView
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
        recyclerView = view.findViewById(R.id.recyclerview_plant)
        recyclerView.adapter = PlantListAdapter(PlantRepository.plantList, mViewPlantLauncher)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }
}