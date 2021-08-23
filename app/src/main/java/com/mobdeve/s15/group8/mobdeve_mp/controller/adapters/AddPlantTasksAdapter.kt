package com.mobdeve.s15.group8.mobdeve_mp.controller.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import java.text.SimpleDateFormat

class AddPlantTasksAdapter(
    private var data: ArrayList<Task>
) : RecyclerView.Adapter<AddPlantTasksAdapter.ViewHolder>() {

    lateinit var taskDeletedListener: OnTaskDeletedListener

    interface OnTaskDeletedListener {
        fun notifyTaskDeleted(task: Task) {}
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val actionTV: TextView = view.findViewById(R.id.tv_action)
        val startDateTV: TextView = view.findViewById(R.id.tv_start_date)
        val repeatTV: TextView = view.findViewById(R.id.tv_repeat)
        val deleteTaskIBtn: ImageButton = view.findViewById(R.id.ibtn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repeatString = "Repeats every ${data[position].repeat} ${data[position].occurrence}/s"

        val f = SimpleDateFormat("MMM d, yyyy")
        holder.actionTV.text = data[position].action
        holder.startDateTV.text = f.format(data[position].startDate)
        holder.repeatTV.text = repeatString
        holder.deleteTaskIBtn.setOnClickListener {
            val task = data[holder.adapterPosition]
            data.remove(task)
            taskDeletedListener.notifyTaskDeleted(task)
            notifyItemRemoved(holder.adapterPosition)
        }

        taskDeletedListener = holder.itemView.context as OnTaskDeletedListener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun addNewTask(task: Task) {
        data.add(task)
        Log.d("AddTask", "Add new: ${data.toString()}")
        notifyItemInserted(data.size-1)
    }
}