package com.mobdeve.s15.group8.mobdeve_mp.model.services

import android.util.Log
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import java.util.*
import kotlin.collections.ArrayList

object TaskService {
    fun findTaskById(id: String): Task? {
        for (task in PlantRepository.taskList)
            if (task.id == id)
                return task
        return null
    }

    fun findTasksByPlantId(plantId: String): ArrayList<Task> {
        val tasks = ArrayList<Task>()
        for (task in PlantRepository.taskList)
            if (task.plantId == plantId)
                tasks.add(task)
        return tasks
    }

    /*
    * Retrieves a list of tasks for today, including those that have been completed
    * */
    fun getTasksToday(): ArrayList<Task> {
        val tasksToday = ArrayList<Task>()
        val dateToday = DateTimeService.getCurrentDateWithoutTime()

        for (task in PlantRepository.taskList) {
            val nextDue = DateTimeService.getNextDueDate(
                task.occurrence,
                task.repeat,
                task.lastCompleted
            )
            Log.d("Dashboard", "${task.action} next due: ${nextDue.time}")
            if (!nextDue.after(dateToday) or (task.lastCompleted == dateToday))
                tasksToday.add(task)
        }
        return tasksToday
    }
}