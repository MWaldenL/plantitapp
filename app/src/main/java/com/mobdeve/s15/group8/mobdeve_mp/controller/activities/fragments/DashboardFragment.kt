package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.LoginActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.DashboardTaskGroupAdapter
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import com.mobdeve.s15.group8.mobdeve_mp.singletons.GoogleSingleton
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {
    private lateinit var btnSignout: Button
    private lateinit var tasksChildren: HashMap<String, ArrayList<String>>
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSignout = view.findViewById(R.id.btn_signout)
        elvTaskGroup = view.findViewById(R.id.elv_task_group)
        tasksChildren =
            mGenerateSampleTasksChildren()  // store the data as a HashMap for ELV rendering
        taskGroupAdapter = DashboardTaskGroupAdapter(requireContext(), tasksChildren)
        elvTaskGroup.setAdapter(taskGroupAdapter)
        mExpandAllGroups()
        btnSignout.setOnClickListener { // sign out from both firebase and google
            F.auth.signOut()
            GoogleSignIn.getClient(this.activity, GoogleSingleton.googleSigninOptions).signOut()
            startActivity(Intent(this@DashboardFragment.context, LoginActivity::class.java))
            this.activity?.finish()
        }
    }

    private fun mExpandAllGroups() {
        for (i in 0 until tasksChildren.size) {
            elvTaskGroup.expandGroup(i)
        }
    }
}