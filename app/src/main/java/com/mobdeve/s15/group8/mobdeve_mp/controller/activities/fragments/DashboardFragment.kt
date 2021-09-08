package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments

import android.content.Intent
import android.net.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.viewing.ProfileActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.DashboardTaskGroupAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.callbacks.DBCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.TaskService

class DashboardFragment : Fragment(), DBCallback {
    private lateinit var elvTaskGroup: ExpandableListView
    private lateinit var taskGroupAdapter: DashboardTaskGroupAdapter
    private lateinit var btnProfile: ImageButton

    private lateinit var tvDashboardHeader: TextView
    private lateinit var tvNoTaskTitle: TextView
    private lateinit var tvNoTaskSubtitle: TextView

    private var mTasks = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlantRepository.setOnDataFetchedListener(this)
        taskGroupAdapter = DashboardTaskGroupAdapter(requireContext(), mTasks)
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
        if (PlantRepository.plantList.isNotEmpty() and PlantRepository.taskList.isNotEmpty()) {
            mLoadTasks()
        }

        mSetViews()
    }

    override fun onResume() {
        super.onResume()
        mSetViews()
    }

    private fun mSetViews() {
        if (TaskService.getTasksToday().size == 0) {
            tvDashboardHeader.visibility = View.INVISIBLE

            tvNoTaskTitle.visibility = View.VISIBLE
            tvNoTaskSubtitle.visibility = View.VISIBLE
        } else {
            tvDashboardHeader.visibility = View.VISIBLE

            tvNoTaskTitle.visibility = View.GONE
            tvNoTaskSubtitle.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnProfile = view.findViewById(R.id.ibtn_profile)
        elvTaskGroup = view.findViewById(R.id.elv_task_group)
        elvTaskGroup.setAdapter(taskGroupAdapter)

        tvDashboardHeader = view.findViewById(R.id.tv_dashboard_header)
        tvNoTaskTitle = view.findViewById(R.id.tv_no_task_title)
        tvNoTaskSubtitle = view.findViewById(R.id.tv_no_task_subtitle)
    }

    private fun mLoadTasks() {
        mTasks = TaskService.getTasksToday()
        taskGroupAdapter.updateData(mTasks)
        mExpandIncompleteGroups()
        btnProfile.setOnClickListener {
            Log.d("MPDashboardFragment", "btnProfile clicked")
            startActivity(Intent(this@DashboardFragment.activity, ProfileActivity::class.java))
        }
    }

    private fun mExpandIncompleteGroups() {
        for (i in 0 until taskGroupAdapter.taskKeys.size) {
            if (!taskGroupAdapter.groupIsCompleted(i))
                elvTaskGroup.expandGroup(i)
        }
    }

    override fun onComplete(tag: String) {
        Log.d("MPDashboard", "DashboardFragment: onComplete $tag")
        if (PlantRepository.plantList.isNotEmpty() and PlantRepository.taskList.isNotEmpty())
            mLoadTasks()
    }
}