package com.mobdeve.s15.group8.mobdeve_mp.controller.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.NotificationService
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders.TaskViewHolder
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.PlantService
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F

class TaskListAdapter(private val data: ArrayList<Task>):
    RecyclerView.Adapter<TaskViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_task_view, parent, false)
        return TaskViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val cvPlantTaskItem: CardView = holder.itemView.findViewById(R.id.cv_plant_task_item)
        val task = data[position]
        val today = DateTimeService.getCurrentDateWithoutTime()

        // bind task text/checked to viewholder
        val last = position == itemCount-1
        holder.bindData(data[position], last, PlantService.findPlantById(data[position].plantId)?.death)

        cvPlantTaskItem.setOnClickListener {
            val index = PlantRepository.taskList.indexOf(task)

            if (task.lastCompleted != today.time) {
                task.lastCompleted = today.time
                PlantRepository.taskList[index] = task

                // update db
                DBService.updateDocument(
                    collection = F.tasksCollection,
                    id = task.id,
                    field = "lastCompleted",
                    value = today.time
                )

                Log.d("hello", "checked this")
                NotificationService.sendCompleteNotification(holder.itemView.context)
            } else {
                val lastDue = DateTimeService.getLastDueDate(
                    task.occurrence,
                    task.repeat,
                    today.time,
                    task.weeklyRecurrence
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

            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}