package com.mobdeve.s15.group8.mobdeve_mp.controller.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.mobdeve.s15.group8.mobdeve_mp.R

class DashboardTaskGroupAdapter(
    private val context: Context,
    private val tasksChildren: HashMap<String, ArrayList<String>>,
    private val tasksTitles: ArrayList<String> = ArrayList(tasksChildren.keys)
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return tasksTitles.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return tasksChildren[tasksTitles[groupPosition]]!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return tasksTitles[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): String {
        return tasksChildren[tasksTitles[groupPosition]]!![childPosition]
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

        // update view content
        val tvGroupTask: TextView = cv!!.findViewById(R.id.tv_group_task)
        tvGroupTask.text = groupListText
        mUpdateExpandedIndicator(isExpanded, cv)

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

        // update view content
        val checkboxDashboardPlant: CheckBox = cv!!.findViewById(R.id.checkbox_dashboard_plant)
        checkboxDashboardPlant.text = childListText

        // remove item from the elv
        checkboxDashboardPlant.setOnClickListener {
            if (checkboxDashboardPlant.isChecked) {
                tasksChildren[getGroup(groupPosition) as String]?.removeAt(childPosition)
                checkboxDashboardPlant.isChecked = false
            }
            // TODO: dito ba ung pagupdate sa db?
            notifyDataSetChanged()
        }

        return cv
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    private fun mUpdateExpandedIndicator(isExpanded: Boolean, cv: View) {
        if (isExpanded)
            cv.findViewById<ImageView>(R.id.iv_expand_group)
                .setImageResource(R.drawable.ic_baseline_expand_less_24)
        else
            cv.findViewById<ImageView>(R.id.iv_expand_group)
                .setImageResource(R.drawable.ic_baseline_expand_more_24)
    }

}