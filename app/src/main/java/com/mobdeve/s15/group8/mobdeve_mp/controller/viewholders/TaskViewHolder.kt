package com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService

class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    // private val cbTask: CheckBox = itemView.findViewById(R.id.cb_task)
    private val tvPlantTask: TextView = itemView.findViewById(R.id.tv_plant_task)
    private val ivPlantTaskIcon: ImageView = itemView.findViewById(R.id.iv_plant_task_icon)
    private val cvPlantTaskItem: CardView = itemView.findViewById(R.id.cv_plant_task_item)

    @SuppressLint("ResourceAsColor")
    fun bindData(task: Task, last: Boolean, death: Boolean?) {
        tvPlantTask.text = task.action

        val today = DateTimeService.getCurrentDateWithoutTime()

        if (death == true) {
            when (task.action) {
                itemView.resources.getStringArray(R.array.actions_array)[0] ->
                    ivPlantTaskIcon.setImageResource(R.drawable.ic_water_filled_24)
                itemView.resources.getStringArray(R.array.actions_array)[1] ->
                    ivPlantTaskIcon.setImageResource(R.drawable.ic_shovel_24)
                itemView.resources.getStringArray(R.array.actions_array)[2] ->
                    ivPlantTaskIcon.setImageResource(R.drawable.ic_prune_24)
                itemView.resources.getStringArray(R.array.actions_array)[3] ->
                    ivPlantTaskIcon.setImageResource(R.drawable.ic_sunlight_24)
                itemView.resources.getStringArray(R.array.actions_array)[4] ->
                    ivPlantTaskIcon.setImageResource(R.drawable.ic_dark_24)
                itemView.resources.getStringArray(R.array.actions_array)[5] ->
                    ivPlantTaskIcon.setImageResource(R.drawable.ic_fertilize_24)
            }

            tvPlantTask.setTextColor(
                ResourcesCompat.getColor(itemView.resources, R.color.primary_color, null))
            ivPlantTaskIcon.setColorFilter(
                ResourcesCompat.getColor(itemView.resources, R.color.gray, null))
            cvPlantTaskItem.setCardBackgroundColor(
                ResourcesCompat.getColor(itemView.resources, R.color.dark_gray, null))
            cvPlantTaskItem.isClickable = false
            cvPlantTaskItem.foreground = null
        } else if (today.time == task.lastCompleted) {
            ivPlantTaskIcon.setImageResource(R.drawable.ic_check_24)

            ivPlantTaskIcon.setColorFilter(
                ResourcesCompat.getColor(itemView.resources, R.color.green, null))
            cvPlantTaskItem.setCardBackgroundColor(
                ResourcesCompat.getColor(itemView.resources, R.color.light_green, null))
            tvPlantTask.setTextColor(
                ResourcesCompat.getColor(itemView.resources, R.color.primary_color, null))
        } else {
            when (task.action) {
                itemView.resources.getStringArray(R.array.actions_array)[0] ->
                    ivPlantTaskIcon.setImageResource(R.drawable.ic_water_filled_24)
                itemView.resources.getStringArray(R.array.actions_array)[1] ->
                    ivPlantTaskIcon.setImageResource(R.drawable.ic_shovel_24)
                itemView.resources.getStringArray(R.array.actions_array)[2] ->
                    ivPlantTaskIcon.setImageResource(R.drawable.ic_prune_24)
                itemView.resources.getStringArray(R.array.actions_array)[3] ->
                    ivPlantTaskIcon.setImageResource(R.drawable.ic_sunlight_24)
                itemView.resources.getStringArray(R.array.actions_array)[4] ->
                    ivPlantTaskIcon.setImageResource(R.drawable.ic_dark_24)
                itemView.resources.getStringArray(R.array.actions_array)[5] ->
                    ivPlantTaskIcon.setImageResource(R.drawable.ic_fertilize_24)
            }

            tvPlantTask.setTextColor(
                ResourcesCompat.getColor(itemView.resources, R.color.white, null))
            ivPlantTaskIcon.setColorFilter(
                ResourcesCompat.getColor(itemView.resources, R.color.white, null))
            cvPlantTaskItem.setCardBackgroundColor(
                ResourcesCompat.getColor(itemView.resources, R.color.green, null))
        }


        val scale = itemView.resources.displayMetrics.density
        val pixels = (16 * scale + 0.5).toInt()
        if (last)
            (itemView.layoutParams as ViewGroup.MarginLayoutParams)
                .setMargins(pixels,0,pixels,0)
        else
            (itemView.layoutParams as ViewGroup.MarginLayoutParams)
                .setMargins(pixels,0,0,0)
        /*val today = DateTimeService.getCurrentDateWithoutTime()
        if (today.time == task.lastCompleted) {
            cbTask.isChecked = true
            cbTask.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            cbTask.isChecked = false
            cbTask.paintFlags = 0
        }*/
    }
}