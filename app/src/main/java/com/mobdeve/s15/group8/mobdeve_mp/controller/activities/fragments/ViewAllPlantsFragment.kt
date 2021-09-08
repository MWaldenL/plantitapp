package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.PlantListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.callbacks.NewPlantCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.NewPlantInstance
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.singletons.LayoutType

class ViewAllPlantsFragment: Fragment(), NewPlantCallback {
    private lateinit var ibGridView: ImageButton
    private lateinit var recyclerViewAlive: RecyclerView
    private lateinit var recyclerViewDead: RecyclerView
    private lateinit var mSharedPref: SharedPreferences
    private lateinit var mEditor: SharedPreferences.Editor
    private lateinit var plantAliveAdapter: PlantListAdapter
    private lateinit var plantDeadAdapter: PlantListAdapter
    private lateinit var tvAlive: TextView
    private lateinit var tvDead: TextView
    private lateinit var tvNoPlantsTitle: TextView
    private lateinit var tvNoPlantsSubtitle: TextView
    private lateinit var tvNoPlantsSubtitle2: TextView
    private lateinit var ivNoPLantsImage: ImageView

    private var mPlantListViewType = LayoutType.GRID_VIEW.ordinal // default to grid view
    private var mAlive = arrayListOf<Plant>()
    private var mDead = arrayListOf<Plant>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_all_plants, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ibGridView = view.findViewById(R.id.ib_gridview)
        recyclerViewAlive = view.findViewById(R.id.recyclerview_plant)
        recyclerViewDead = view.findViewById(R.id.recyclerview_dead)
        tvAlive = view.findViewById(R.id.tv_alive)
        tvDead = view.findViewById(R.id.tv_dead)
        tvNoPlantsTitle = view.findViewById(R.id.tv_no_plants_title)
        tvNoPlantsSubtitle = view.findViewById(R.id.tv_no_plants_subtitle)
        tvNoPlantsSubtitle2 = view.findViewById(R.id.tv_no_plants_subtitle_2)
        ivNoPLantsImage = view.findViewById(R.id.iv_no_plants_bg)

        // Setup listeners
        NewPlantInstance.setOnNewPlantListener(this) // listen for new plant added
        ibGridView.setOnClickListener { mToggleLayout() }

        // Setup shared preferences
        mSharedPref = this
            .requireActivity()
            .getSharedPreferences(getString(R.string.SP_NAME), Context.MODE_PRIVATE)
        mEditor = mSharedPref.edit()
        mPlantListViewType = mSharedPref.getInt(getString(R.string.SP_VIEW_KEY), 0)

        ibGridView.setImageResource(if (mPlantListViewType == LayoutType.LINEAR_VIEW.ordinal)
            R.drawable.ic_bento_24 else
            R.drawable.ic_card_24)
        mRefreshRecyclerViews()
    }

    private fun mToggleLayout() {
        mPlantListViewType = 1 - mPlantListViewType
        mEditor.putInt(getString(R.string.SP_VIEW_KEY), mPlantListViewType)
        mEditor.apply()
        ibGridView.setImageResource(if (mPlantListViewType == LayoutType.LINEAR_VIEW.ordinal)
            R.drawable.ic_bento_24 else
            R.drawable.ic_card_24)
        mRefreshRecyclerViews()
    }

    private fun mRefreshRecyclerViews() {
        plantAliveAdapter = PlantListAdapter(mAlive)
        plantDeadAdapter = PlantListAdapter(mDead)
        plantAliveAdapter.viewType = mPlantListViewType
        plantDeadAdapter.viewType = mPlantListViewType
        recyclerViewAlive.adapter = plantAliveAdapter
        recyclerViewDead.adapter = plantDeadAdapter
        recyclerViewAlive.layoutManager = if (mPlantListViewType == LayoutType.LINEAR_VIEW.ordinal)
            LinearLayoutManager(requireContext()) else
            GridLayoutManager(requireContext(), 2)
        recyclerViewDead.layoutManager = if (mPlantListViewType == LayoutType.LINEAR_VIEW.ordinal)
            LinearLayoutManager(requireContext()) else
            GridLayoutManager(requireContext(), 2)
    }

    override fun onStart() {
        super.onStart()
        mPlantListViewType = mSharedPref.getInt(getString(R.string.SP_VIEW_KEY), mPlantListViewType)
        mDead.clear()
        mAlive.clear()
        for (plant in PlantRepository.plantList) {
            if (plant.death)
                mDead.add(plant)
            else
                mAlive.add(plant)
        }

        mSetViews()
        onPlantAdded()
    }

    override fun onResume() {
        super.onResume()
        mPlantListViewType = mSharedPref.getInt(getString(R.string.SP_VIEW_KEY), mPlantListViewType)
        mSetViews()
    }

    override fun onPlantAdded() {
        recyclerViewAlive.adapter?.notifyDataSetChanged()
        recyclerViewDead.adapter?.notifyDataSetChanged()
    }

    private fun mSetViews() {
        if (mDead.size == 0)
            tvDead.visibility = View.GONE

        if (mDead.size == 0 && mAlive.size == 0) {
            tvAlive.visibility = View.INVISIBLE
            ibGridView.visibility = View.INVISIBLE

            tvNoPlantsTitle.visibility = View.VISIBLE
            tvNoPlantsSubtitle.visibility = View.VISIBLE
            tvNoPlantsSubtitle2.visibility = View.VISIBLE
            ivNoPLantsImage.visibility = View.VISIBLE
        } else {
            tvAlive.visibility = View.VISIBLE
            ibGridView.visibility = View.VISIBLE

            tvNoPlantsTitle.visibility = View.GONE
            tvNoPlantsSubtitle.visibility = View.GONE
            tvNoPlantsSubtitle2.visibility = View.GONE
            ivNoPLantsImage.visibility = View.GONE
        }
    }
}