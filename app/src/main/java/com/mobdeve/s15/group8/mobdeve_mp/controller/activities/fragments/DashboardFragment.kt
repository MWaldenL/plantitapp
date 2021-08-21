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
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.PlantService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.TaskService
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DashboardFragment : Fragment(), DBCallback {
    private var mTasks: HashMap<String, ArrayList<Plant?>> = HashMap()

    private var tasksLoaded = false
    private var plantsLoaded = false

    private lateinit var elvTaskGroup: ExpandableListView
    private lateinit var taskGroupAdapter: DashboardTaskGroupAdapter

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

    override fun onStart() {
        super.onStart()
        if (PlantRepository.plantList.isNotEmpty() and PlantRepository.taskList.isNotEmpty())
            mLoadTasks()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        elvTaskGroup = view.findViewById(R.id.elv_task_group)
        taskGroupAdapter = DashboardTaskGroupAdapter(requireContext(), mTasks)
        elvTaskGroup.setAdapter(taskGroupAdapter)
    }

    private fun mLoadTasks() {
        mTasks = HashMap()
        val tasksToday = TaskService.getTasksToday()
        Log.d("Dashboard", "tasksToday: $tasksToday")
        for (task in tasksToday) {
            val plant = PlantService.findPlantById(task.plantId)
            // create a new arraylist of plants associated with the task
            if (mTasks[task.action] == null)
                mTasks[task.action] = ArrayList()
            mTasks[task.action]?.add(plant)
        }
        taskGroupAdapter.updateTaskData(mTasks)
        mExpandAllGroups()
    }

    private fun mExpandAllGroups() {
        for (i in 0 until mTasks.size) {
            elvTaskGroup.expandGroup(i)
        }
    }

    override fun onDataRetrieved(doc: MutableMap<String, Any>, id: String, type: String) {
    }

    override fun onDataRetrieved(docs: ArrayList<MutableMap<String, Any>>, type: String) {
    }

    override fun onComplete(tag: String) {
        Log.d("Dashboard", "DashboardFragment: onComplete $tag")
        if (PlantRepository.plantList.isNotEmpty() and PlantRepository.taskList.isNotEmpty())
            mLoadTasks()
    }
}