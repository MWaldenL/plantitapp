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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DashboardFragment : Fragment(), DBCallback {
    private var mTasks: HashMap<String, ArrayList<Plant?>> = HashMap()

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
        taskGroupAdapter = DashboardTaskGroupAdapter(requireContext(), mTasks)
        elvTaskGroup.setAdapter(taskGroupAdapter)
    }

    override fun onStart() {
        super.onStart()
        mLoadTasks()
    }

    private fun mLoadTasks() {
        mTasks = HashMap()
        for ((plantId, tasks) in PlantRepository.tasksToday) {
            val plant = PlantService.findPlantById(plantId)
            for (task in tasks) {
                // create a new arraylist of plants associated with the task
                if (mTasks[task.action] == null)
                    mTasks[task.action] = ArrayList()

                mTasks[task.action]?.add(plant)
            }
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
        if (tag == PlantRepository.TASKS_TYPE) {

            Log.d("Dashboard", PlantRepository.taskList.toString())
            Log.d("Dashboard", "Loaded")

            /*PlantRepository.setOnDataFetchedListener(null)
            mLoadTasks()*/
        }
    }
}