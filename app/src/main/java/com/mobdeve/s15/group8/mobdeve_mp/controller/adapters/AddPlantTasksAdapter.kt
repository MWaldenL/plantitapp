package com.mobdeve.s15.group8.mobdeve_mp.controller.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddPlantTasksAdapter(
    private val mContext: Context,
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
        val ivAddTaskIcon: ImageView = view.findViewById(R.id.iv_add_task_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var repeatString = ""
        if (data[position].occurrence == "Week") {
            repeatString = "Repeats every "

            data[position].weeklyRecurrence!!.forEachIndexed { idx, day ->
                if (day == 1)
                    repeatString += "Sunday"
                else if (day == 2)
                    repeatString += "Monday"
                else if (day == 3)
                    repeatString += "Tuesday"
                else if (day == 4)
                    repeatString += "Wednesday"
                else if (day == 5)
                    repeatString += "Thursday"
                else if (day == 6)
                    repeatString += "Friday"
                else if (day == 7)
                    repeatString += "Saturday"

                if (idx != data[position].weeklyRecurrence!!.size - 1)
                    repeatString += ", "
            }
        } else {
            repeatString = "Repeats every ${data[position].repeat} " +
                    data[position].occurrence.lowercase(Locale.getDefault())
            if (data[position].repeat > 1)
                repeatString += "s"
        }


        val f = SimpleDateFormat("MMM d, yyyy")
        holder.actionTV.text = data[position].action
        holder.startDateTV.text = f.format(data[position].startDate)
        holder.repeatTV.text = repeatString
        holder.deleteTaskIBtn.setOnClickListener {
            val task = data[holder.adapterPosition]
            data.remove(task)
            notifyItemRemoved(holder.adapterPosition)
            taskDeletedListener.notifyTaskDeleted(task)
        }

        when (data[position].action) {
            mContext.resources.getStringArray(R.array.actions_array)[0] ->
                holder.ivAddTaskIcon.setImageResource(R.drawable.ic_water_filled_24)
            mContext.resources.getStringArray(R.array.actions_array)[1] ->
                holder.ivAddTaskIcon.setImageResource(R.drawable.ic_shovel_24)
            mContext.resources.getStringArray(R.array.actions_array)[2] ->
                holder.ivAddTaskIcon.setImageResource(R.drawable.ic_prune_24)
            mContext.resources.getStringArray(R.array.actions_array)[3] ->
                holder.ivAddTaskIcon.setImageResource(R.drawable.ic_sunlight_24)
            mContext.resources.getStringArray(R.array.actions_array)[4] ->
                holder.ivAddTaskIcon.setImageResource(R.drawable.ic_dark_24)
            mContext.resources.getStringArray(R.array.actions_array)[5] ->
                holder.ivAddTaskIcon.setImageResource(R.drawable.ic_fertilize_24)
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