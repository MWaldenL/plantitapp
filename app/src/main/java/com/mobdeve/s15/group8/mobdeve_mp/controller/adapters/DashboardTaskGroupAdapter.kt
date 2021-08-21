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
import com.google.firebase.firestore.FieldValue
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.PlantService
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DashboardTaskGroupAdapter(
    private var mContext: Context,
    private var mTasks: ArrayList<Task>
) : BaseExpandableListAdapter() {

    lateinit var taskMaps: HashMap<String, ArrayList<String>>
    lateinit var taskKeys: ArrayList<String>

    init {
        mLoadTaskMaps()
    }

    private fun mLoadTaskMaps() {

        taskMaps = HashMap()

        for (task in mTasks) {
            PlantService.findPlantById(task.plantId)?.let{
                if (taskMaps[task.action] == null)
                    taskMaps[task.action] = ArrayList()
                taskMaps[task.action]!!.add(it.id)
            }
        }

        taskKeys = ArrayList(taskMaps.keys)
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

    override fun getChild(groupPosition: Int, childPosition: Int): String {
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
        val groupListText: String = getGroup(groupPosition) as String
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
        mUpdatePlantsLeft(groupPosition, cv)

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

        return cv
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val childListText = PlantService.findPlantById(getChild(groupPosition, childPosition))?.name
        var cv = convertView
        if (cv == null) {
            val layoutInflater: LayoutInflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            cv = layoutInflater.inflate(R.layout.item_dashboard_plant, null)
        }

        // update view content
        val checkboxDashboardPlant: CheckBox = cv!!.findViewById(R.id.checkbox_dashboard_plant)
        checkboxDashboardPlant.text = childListText

        // remove item from the elv
        checkboxDashboardPlant.setOnClickListener {
            if (checkboxDashboardPlant.isChecked) {
                checkboxDashboardPlant.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG



                /*val currDate = DateTimeService.getCurrentDateWithoutTime().time
                val plant = tasks[taskTitles[groupPosition]]!![childPosition]
                val completedTask = PlantService.findTaskByAction(taskTitles[groupPosition], plant!!)*/

                /*mUpdateLastCompletedTask(
                    currDate,
                    plant,
                    completedTask!!
                )*/

//                val idx = PlantRepository.tasksToday[completedTask?.action]!!.indexOf(completedTask)
//                Log.d("Dashboard", "Index = $idx")
//                PlantRepository.tasksToday[completedTask.action]!![idx].lastCompleted = currDate
            } else {
                checkboxDashboardPlant.paintFlags = 0

                /*val plant = tasks[taskTitles[groupPosition]]!![childPosition]!!
                val revertedTask = TaskService.findTaskById(plant.tasks)
                mUpdateLastCompletedTask(
                    DateTimeService.getLastDueDate(revertedTask!!, Date()).time,
                    plant,
                    revertedTask
                )*/
            }
            // TODO: update plantrepo?

            notifyDataSetChanged()
        }

        return cv
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    private fun mUpdateLastCompletedTask(newDate: Date, plant: Plant, completedTask: Task) {
        // TODO: update

        val toUpdate: HashMap<*,*> = hashMapOf(
            "action" to completedTask.action,
            "lastCompleted" to completedTask.lastCompleted,
            "occurrence" to completedTask.occurrence,
            "repeat" to completedTask.repeat,
            "startDate" to completedTask.startDate
        )

        DBService.updateDocument(
            collection = F.plantsCollection,
            id = plant.id,
            field = "tasks",
            value = FieldValue.arrayRemove(toUpdate)
        )
        completedTask.lastCompleted = newDate
        DBService.updateDocument(
            collection = F.plantsCollection,
            id = plant.id,
            field = "tasks",
            value = FieldValue.arrayUnion(completedTask)
        )
    }

    private fun mUpdatePlantsLeft(groupPosition: Int, cv: View) {

        // TODO: update

//        val plantsLeft = 0
//        PlantRepository.tasksToday[taskTitles[groupPosition]]
//        for (task in PlantRepository.tasksToday) {
//
//        }

        val plantsLeftString = taskMaps[getGroup(groupPosition) as String]?.size.toString()
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

    fun updateData(data: ArrayList<Task>) {
        mTasks = data
        mLoadTaskMaps()
        notifyDataSetChanged()
    }

}