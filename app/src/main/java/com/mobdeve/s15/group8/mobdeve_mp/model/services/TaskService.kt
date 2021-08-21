package com.mobdeve.s15.group8.mobdeve_mp.model.services

import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository

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

    fun getTasksToday(): ArrayList<Task> {
        val tasksToday = ArrayList<Task>()
        val dateToday = DateTimeService.getCurrentDateWithoutTime()

        for (task in PlantRepository.taskList) {
            val nextDue = DateTimeService.getNextDueDate(
                task.occurrence,
                task.repeat,
                task.lastCompleted
            )
            if (!dateToday.before(nextDue))
                tasksToday.add(task)
        }

        return tasksToday
    }
}