package com.mobdeve.s15.group8.mobdeve_mp.controller.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FieldValue
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.PlantService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.TaskService
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DashboardTaskGroupAdapter(
    private var mContext: Context,
    private var mTasks: ArrayList<Task>
) : BaseExpandableListAdapter() {

    lateinit var taskMaps: HashMap<String, ArrayList<HashMap<String,String>>>
    lateinit var taskKeys: ArrayList<String>

    init {
        mLoadTaskMaps()
    }

    override fun getGroupCount(): Int {
        return taskKeys.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return taskMaps[taskKeys[groupPosition]]!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return taskKeys[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): HashMap<String,String> {
        return taskMaps[taskKeys[groupPosition]]!![childPosition]
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
        val dateToday = DateTimeService.getCurrentDateWithoutTime().time
        val groupListText = getGroup(groupPosition) as String
        var cv = convertView
        if (cv == null) {
            val layoutInflater: LayoutInflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            cv = layoutInflater.inflate(R.layout.group_dashboard_tasks, null)
        }

        // update view content
        val tvGroupTask: TextView = cv!!.findViewById(R.id.tv_group_task)
        tvGroupTask.text = groupListText
        mUpdateExpandedIndicator(isExpanded, cv)

        // change icons
        val ivTaskIcon: ImageView = cv.findViewById(R.id.iv_task_icon)
        when (groupListText) {
            mContext.resources.getStringArray(R.array.actions_array)[0] ->
                ivTaskIcon.setImageResource(R.drawable.ic_water_filled_24)
            mContext.resources.getStringArray(R.array.actions_array)[1] ->
                ivTaskIcon.setImageResource(R.drawable.ic_shovel_24)
            mContext.resources.getStringArray(R.array.actions_array)[2] ->
                ivTaskIcon.setImageResource(R.drawable.ic_prune_24)
            mContext.resources.getStringArray(R.array.actions_array)[3] ->
                ivTaskIcon.setImageResource(R.drawable.ic_sunlight_24)
            mContext.resources.getStringArray(R.array.actions_array)[4] ->
                ivTaskIcon.setImageResource(R.drawable.ic_dark_24)
            mContext.resources.getStringArray(R.array.actions_array)[5] ->
                ivTaskIcon.setImageResource(R.drawable.ic_fertilize_24)
        }

        // update plants left
        var plantsLeft = 0
        val tvPlantsLeft: TextView = cv.findViewById(R.id.tv_plants_left)
        for (plantTask in taskMaps[taskKeys[groupPosition]]!!) {
            val task = plantTask["taskId"]?.let { TaskService.findTaskById(it) }
            if (task != null)
                if (task.lastCompleted != dateToday)
                    plantsLeft += 1
        }
        if (plantsLeft > 0) {
            tvPlantsLeft.visibility = View.VISIBLE
            tvPlantsLeft.text = plantsLeft.toString()
        } else {
            tvPlantsLeft.visibility = View.INVISIBLE
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
        val dateToday = DateTimeService.getCurrentDateWithoutTime()
        val child = getChild(groupPosition, childPosition)
        val plant = child["plantId"]?.let { PlantService.findPlantById(it) }
        val task = child["taskId"]?.let { TaskService.findTaskById(it) }
        var cv = convertView
        if (cv == null) {
            val layoutInflater: LayoutInflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            cv = layoutInflater.inflate(R.layout.item_dashboard_plant, null)
        }

        // update checkbox view
        val checkboxDashboardPlant: CheckBox = cv!!.findViewById(R.id.checkbox_dashboard_plant)
        val tvLate: TextView = cv.findViewById(R.id.tv_late)

        checkboxDashboardPlant.text = plant?.name

        // check if task is late
        if (task?.let { TaskService.taskIsLate(it) } == true)
            tvLate.visibility = View.VISIBLE
        else
            tvLate.visibility = View.INVISIBLE

        // check if task has been completed
        if (dateToday.time == task!!.lastCompleted) {
            checkboxDashboardPlant.isChecked = true
            checkboxDashboardPlant.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            checkboxDashboardPlant.isChecked = false
            checkboxDashboardPlant.paintFlags = 0
        }

        // remove item from the elv
        checkboxDashboardPlant.setOnClickListener {
            if (checkboxDashboardPlant.isChecked) {
                // update repo
                task.lastCompleted = dateToday.time
                // update db
                DBService.updateDocument(
                    collection = F.tasksCollection,
                    id = task.id,
                    field = "lastCompleted",
                    value = dateToday.time
                )
            } else {
                val lastDue = DateTimeService.getLastDueDate(
                    task.occurrence,
                    task.repeat,
                    dateToday.time
                ).time
                // update repo
                task.lastCompleted = lastDue
                // update db
                DBService.updateDocument(
                    collection = F.tasksCollection,
                    id = task.id,
                    field = "lastCompleted",
                    value = lastDue
                )
            }
            notifyDataSetChanged()
        }

        return cv
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    fun updateData(data: ArrayList<Task>) {
        mTasks = data
        mLoadTaskMaps()
        notifyDataSetChanged()
    }

    fun groupIsCompleted(groupPosition: Int): Boolean {
        val currDate = DateTimeService.getCurrentDateWithoutTime().time
        for (taskMap in taskMaps[taskKeys[groupPosition]]!!) {
            val task = TaskService.findTaskById(taskMap["taskId"].toString())
            if (task != null)
                if (task.lastCompleted != currDate)
                    return false
        }
        return true
    }

    private fun mUpdateExpandedIndicator(isExpanded: Boolean, cv: View) {
        if (isExpanded)
            cv.findViewById<ImageView>(R.id.iv_expand_group)
                .setImageResource(R.drawable.ic_expand_less_24)
        else
            cv.findViewById<ImageView>(R.id.iv_expand_group)
                .setImageResource(R.drawable.ic_expand_more_24)
    }

    private fun mLoadTaskMaps() {
        taskMaps = HashMap()

        for (task in mTasks) {
            PlantService.findPlantById(task.plantId)?.let{
                if (taskMaps[task.action] == null)
                    taskMaps[task.action] = ArrayList()
                taskMaps[task.action]!!.add(hashMapOf(
                    "plantId" to it.id,
                    "taskId" to task.id
                ))
            }
        }

        taskKeys = ArrayList(taskMaps.keys)
    }

}