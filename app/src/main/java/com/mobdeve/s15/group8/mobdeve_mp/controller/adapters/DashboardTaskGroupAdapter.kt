package com.mobdeve.s15.group8.mobdeve_mp.controller.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.mobdeve.s15.group8.mobdeve_mp.R

class DashboardTaskGroupAdapter(
    private val context: Context,
    private val taskGroups: ArrayList<String>,
    private val tasksChildren: HashMap<String, ArrayList<String>>
    ) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return taskGroups.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return tasksChildren[taskGroups[groupPosition]]!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return taskGroups[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): String {
        return tasksChildren[taskGroups[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
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
            val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            cv = layoutInflater.inflate(R.layout.group_dashboard_tasks, null)
        }
        val tvGroupTask: TextView = cv!!.findViewById(R.id.tv_group_task)
        tvGroupTask.text = groupListText

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
            val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            cv = layoutInflater.inflate(R.layout.item_dashboard_plant, null)
        }
        val tvPlantTaskItem: TextView = cv!!.findViewById(R.id.tv_item_task_plant)
        tvPlantTaskItem.text = childListText

        return cv
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}