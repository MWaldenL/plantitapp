package com.mobdeve.s15.group8.mobdeve_mp.controller.adapters

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders.TaskViewHolder
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F

class TaskListAdapter(
    private val data: ArrayList<Task>):
    RecyclerView.Adapter<TaskViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_task_view, parent, false)
        return TaskViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = data[position]
        val cbTask = holder.itemView.findViewById<CheckBox>(R.id.cb_task)
        val today = DateTimeService.getCurrentDateWithoutTime()

        // bind task text/checked to viewholder
        holder.bindData(data[position])

        cbTask.setOnClickListener {
            val index = PlantRepository.taskList.indexOf(task)

            if (cbTask.isChecked) {
                task.lastCompleted = today.time
                PlantRepository.taskList[index] = task

                // update db
                DBService.updateDocument(
                    collection = F.tasksCollection,
                    id = task.id,
                    field = "lastCompleted",
                    value = today.time
                )
            } else {
                val lastDue = DateTimeService.getLastDueDate(
                    task.occurrence,
                    task.repeat,
                    today.time
                ).time

                // update repo
                task.lastCompleted = lastDue
                PlantRepository.taskList[index] = task

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
    }

    override fun getItemCount(): Int {
        return data.size
    }
}