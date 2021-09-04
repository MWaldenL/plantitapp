package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.LoginActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.ProfileActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.DashboardTaskGroupAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.DBCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.TaskService
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import com.mobdeve.s15.group8.mobdeve_mp.singletons.GoogleSingleton

class DashboardFragment : Fragment(), DBCallback {
    private lateinit var elvTaskGroup: ExpandableListView
    private lateinit var taskGroupAdapter: DashboardTaskGroupAdapter
    private var mTasks = ArrayList<Task>()
    private lateinit var btnSignout: Button
    private lateinit var btnProfile: ImageButton

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
        if (PlantRepository.plantList.isNotEmpty() and PlantRepository.taskList.isNotEmpty())
            mLoadTasks()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSignout = view.findViewById(R.id.btn_signout)
        btnProfile = view.findViewById(R.id.ibtn_profile)
        elvTaskGroup = view.findViewById(R.id.elv_task_group)
        elvTaskGroup.setAdapter(taskGroupAdapter)
    }

    private fun mLoadTasks() {
        mTasks = TaskService.getTasksToday()
        taskGroupAdapter.updateData(mTasks)
        mExpandIncompleteGroups()
        btnSignout.setOnClickListener { // sign out from both firebase and google
            PlantRepository.resetData()
            F.auth.signOut()
            GoogleSignIn.getClient(this.activity, GoogleSingleton.googleSigninOptions).signOut()
            Log.d("Dashboard", "Logging out")
            startActivity(Intent(this@DashboardFragment.activity, LoginActivity::class.java))
            requireActivity().finish()
        }

        btnProfile.setOnClickListener {
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
        Log.d("Dashboard", "DashboardFragment: onComplete $tag")
        if (PlantRepository.plantList.isNotEmpty() and PlantRepository.taskList.isNotEmpty())
            mLoadTasks()
    }
}