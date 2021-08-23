package com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders

import android.graphics.Paint
import android.view.View
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService

class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val cbTask: CheckBox = itemView.findViewById(R.id.cb_task)

    fun bindData(task: Task) {
        cbTask.text = task.action

        val today = DateTimeService.getCurrentDateWithoutTime()
        if (today.time == task.lastCompleted) {
            cbTask.isChecked = true
            cbTask.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            cbTask.isChecked = false
            cbTask.paintFlags = 0
        }
    }
}