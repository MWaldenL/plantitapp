package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.DashboardTaskGroupAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.DBCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.PlantTaskService
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DashboardFragment : Fragment(), DBCallback {
    private var mTasksChildren: HashMap<String, ArrayList<String>> = HashMap()

    private lateinit var elvTaskGroup: ExpandableListView
    private lateinit var taskGroupAdapter: DashboardTaskGroupAdapter

    private fun mGenerateSampleTasksChildren(): HashMap<String, ArrayList<String>> {
        val sampleTaskGroup: HashMap<String, ArrayList<String>> = HashMap()

        val grpKeys = arrayListOf(*resources.getStringArray(R.array.actions_array))
        for (key in grpKeys) {

            val plants: ArrayList<String> = ArrayList()
            plants.add("Snake Plant")
            plants.add("Oregano")
            plants.add("Basil")
            plants.add("San Francisco")
            plants.add("Johnny")

            sampleTaskGroup[key] = plants
        }

        return sampleTaskGroup
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlantRepository.setOnDataFetchedListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        elvTaskGroup = view.findViewById(R.id.elv_task_group)
        taskGroupAdapter = DashboardTaskGroupAdapter(requireContext(), mTasksChildren)
        elvTaskGroup.setAdapter(taskGroupAdapter)
    }

    override fun onStart() {
        super.onStart()
        mLoadTasks()
    }

    private fun mLoadTasks() {
        mTasksChildren = HashMap()
        for ((plantId, tasks) in PlantRepository.tasksToday) {
            val plant = PlantTaskService.findPlantById(plantId)
            for (task in tasks) {
                if (mTasksChildren[task.action] == null)
                    mTasksChildren[task.action] = ArrayList()
                // TODO: use nickname if it exists
                if (plant!!.nickname == "")
                    mTasksChildren[task.action]?.add(plant.name)
                else
                    mTasksChildren[task.action]?.add("${plant.name} (${plant.nickname})")
            }
        }
        taskGroupAdapter.updateData(mTasksChildren)
        mExpandAllGroups()
    }

    private fun mExpandAllGroups() {
        for (i in 0 until mTasksChildren.size) {
            elvTaskGroup.expandGroup(i)
        }
    }

    override fun onDataRetrieved(doc: MutableMap<String, Any>, id: String, type: String) {
    }

    override fun onComplete(tag: String) {
        if (tag == PlantRepository.PLANTS_TYPE) {
            PlantRepository.setOnDataFetchedListener(null)
            mLoadTasks()
        }
    }
}