package com.mobdeve.s15.group8.mobdeve_mp.controller.adapters

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.PlantTaskService
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F

class DashboardTaskGroupAdapter(
    private var context: Context,
    private var tasks: HashMap<String, ArrayList<Plant?>>
) : BaseExpandableListAdapter() {

    private lateinit var taskDetails: HashMap<String, ArrayList<String>>
    private lateinit var taskTitles: ArrayList<String>

    init {
        mLoadTasks()
    }

    private fun mLoadTasks() {
        taskDetails = HashMap()
        for ((task, plants) in tasks) {
            taskDetails[task] = ArrayList()
            for (plant in plants)
                taskDetails[task]?.add(plant!!.name)
        }

        taskTitles = ArrayList(tasks.keys)
    }

    override fun getGroupCount(): Int {
        return taskTitles.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return taskDetails[taskTitles[groupPosition]]!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return taskTitles[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): String {
        return taskDetails[taskTitles[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val groupListText: String = getGroup(groupPosition) as String
        var cv = convertView
        if (cv == null) {
            val layoutInflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            cv = layoutInflater.inflate(R.layout.group_dashboard_tasks, null)
        }

        // update view content
        val tvGroupTask: TextView = cv!!.findViewById(R.id.tv_group_task)
        tvGroupTask.text = groupListText
        mUpdateExpandedIndicator(isExpanded, cv)
        mUpdatePlantsLeft(groupPosition, cv)

        val ivTaskIcon: ImageView = cv.findViewById(R.id.iv_task_icon)
        when (groupListText) {
            context.resources.getStringArray(R.array.actions_array)[0] ->
                ivTaskIcon.setImageResource(R.drawable.ic_water_filled_24)
            context.resources.getStringArray(R.array.actions_array)[1] ->
                ivTaskIcon.setImageResource(R.drawable.ic_shovel_24)
            context.resources.getStringArray(R.array.actions_array)[2] ->
                ivTaskIcon.setImageResource(R.drawable.ic_prune_24)
            context.resources.getStringArray(R.array.actions_array)[3] ->
                ivTaskIcon.setImageResource(R.drawable.ic_sunlight_24)
            context.resources.getStringArray(R.array.actions_array)[4] ->
                ivTaskIcon.setImageResource(R.drawable.ic_dark_24)
            context.resources.getStringArray(R.array.actions_array)[5] ->
                ivTaskIcon.setImageResource(R.drawable.ic_fertilize_24)
        }

        return cv
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val childListText: String = getChild(groupPosition, childPosition)
        var cv = convertView
        if (cv == null) {
            val layoutInflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            cv = layoutInflater.inflate(R.layout.item_dashboard_plant, null)
        }

        // update view content
        val checkboxDashboardPlant: CheckBox = cv!!.findViewById(R.id.checkbox_dashboard_plant)
        checkboxDashboardPlant.text = childListText

        // remove item from the elv
        checkboxDashboardPlant.setOnClickListener {
            if (checkboxDashboardPlant.isChecked) {
                checkboxDashboardPlant.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                /*// remove plant from the list of children
                tasksChildren[getGroup(groupPosition) as String]?.removeAt(childPosition)
                // remove task if there are no plants associated with the task
                if (tasksChildren[getGroup(groupPosition) as String]?.size == 0) {
                    val key = getGroup(groupPosition) as String
                    tasksChildren.remove(key)
                    tasksTitles.remove(key)
                }
                checkboxDashboardPlant.isChecked = false*/

                Log.d("Dashboard", "[$groupPosition, $childPosition]")
                val plant = tasks[taskTitles[groupPosition]]!![childPosition]
                val taskToComplete = PlantTaskService.findTaskByAction(taskTitles[groupPosition], plant!!)

                val toUpdate: HashMap<*,*> = hashMapOf(
                    "action" to taskToComplete!!.action,
                    "lastCompleted" to taskToComplete.lastCompleted,
                    "occurrence" to taskToComplete.occurrence,
                    "repeat" to taskToComplete.repeat,
                    "startDate" to taskToComplete.startDate
                )

                DBService.updateDocument(
                    collection = F.plantsCollection,
                    id = plant.id,
                    field = "tasks",
                    value = FieldValue.arrayRemove(toUpdate)
                )
                taskToComplete.lastCompleted = DateTimeService.getCurrentDateWithoutTime().time
                DBService.updateDocument(
                    collection = F.plantsCollection,
                    id = plant.id,
                    field = "tasks",
                    value = FieldValue.arrayUnion(taskToComplete)
                )

                Log.d("Dashboard", plant.toString())

                // TODO: undo

            } else {
                checkboxDashboardPlant.paintFlags = 0
            }
            notifyDataSetChanged()
        }

        return cv
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    private fun mUpdatePlantsLeft(groupPosition: Int, cv: View) {
        val plantsLeftString = taskDetails[getGroup(groupPosition) as String]?.size.toString()
        val tvPlantsLeft: TextView = cv.findViewById(R.id.tv_plants_left)
        tvPlantsLeft.text = plantsLeftString
    }

    private fun mUpdateExpandedIndicator(isExpanded: Boolean, cv: View) {
        if (isExpanded)
            cv.findViewById<ImageView>(R.id.iv_expand_group)
                .setImageResource(R.drawable.ic_baseline_expand_less_24)
        else
            cv.findViewById<ImageView>(R.id.iv_expand_group)
                .setImageResource(R.drawable.ic_baseline_expand_more_24)
    }

    fun updateTaskData(data: HashMap<String, ArrayList<Plant?>>) {
        tasks = data
        mLoadTasks()
        notifyDataSetChanged()
    }

}