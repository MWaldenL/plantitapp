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
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.TaskService
import kotlin.collections.ArrayList

class DashboardFragment : Fragment(), DBCallback {
    private lateinit var elvTaskGroup: ExpandableListView
    private lateinit var taskGroupAdapter: DashboardTaskGroupAdapter
    private var mTasks = ArrayList<Task>()

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
        // btnSignout = view.findViewById(R.id.btn_signout)
        elvTaskGroup = view.findViewById(R.id.elv_task_group)
        taskGroupAdapter = DashboardTaskGroupAdapter(requireContext(), mTasks)
        elvTaskGroup.setAdapter(taskGroupAdapter)
    }

    private fun mLoadTasks() {
        mTasks = TaskService.getTasksToday()
        taskGroupAdapter.updateData(mTasks)
        mExpandIncompleteGroups()
        /*btnSignout.setOnClickListener { // sign out from both firebase and google
            F.auth.signOut()
            GoogleSignIn.getClient(this.activity, GoogleSingleton.googleSigninOptions).signOut()
            startActivity(Intent(this@DashboardFragment.context, LoginActivity::class.java))
            this.activity?.finish()
        }*/
    }

    private fun mExpandIncompleteGroups() {
        for (i in 0 until taskGroupAdapter.taskKeys.size) {
            if (!taskGroupAdapter.groupIsCompleted(i))
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