package com.mobdeve.s15.group8.mobdeve_mp.model.services

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QuerySnapshot
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
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
    fun getTasksToday(includeComplete: Boolean = true): ArrayList<Task> {
        val tasksToday = ArrayList<Task>()
        val dateToday = DateTimeService.getCurrentDateWithoutTime()
        Log.d("MPTaskService", "${PlantRepository.taskList}")
        for (task in PlantRepository.taskList) {
            val nextDue = DateTimeService.getNextDueDate(
                task.occurrence,
                task.repeat,
                task.lastCompleted,
                task.weeklyRecurrence
            )
            Log.d("Dashboard", "${task.lastCompleted} lc vs dt ${dateToday.time}")

            if (!nextDue.after(dateToday) || (includeComplete && task.lastCompleted == dateToday.time))
                tasksToday.add(task)
        }
        Log.d("MPTaskService", "finished getting: $tasksToday")

        return tasksToday
    }

    fun isTasksToday(docs: QuerySnapshot): Boolean {
        val tasksToday = ArrayList<Task>()
        val dateToday = DateTimeService.getCurrentDateWithoutTime()

        for (doc in docs) {
            val data = doc.data
            tasksToday.add(Task(
                id = data["id"].toString(),
                plantId = data["plantId"].toString(),
                userId = data["userId"].toString(),
                action = data["action"].toString(),
                startDate = (data["startDate"] as Timestamp).toDate(),
                repeat = data["repeat"].toString().toInt(),
                occurrence = data["occurrence"].toString(),
                lastCompleted = (data["lastCompleted"] as Timestamp).toDate(),
                weeklyRecurrence = data["weeklyRecurrence"] as ArrayList<Int>?
            ))
        }

        for (task in tasksToday) {
            val nextDue = DateTimeService.getNextDueDate(
                task.occurrence,
                task.repeat,
                task.lastCompleted,
                task.weeklyRecurrence
            )
            Log.d("Dashboard", "${task.lastCompleted} lc vs dt ${dateToday.time}")

            if (!nextDue.after(dateToday) && !PlantService.getPlantDeath(task.plantId))
                return true
        }

        return false
    }

    fun taskIsLate(task: Task): Boolean {
        val dateToday = DateTimeService.getCurrentDateWithoutTime()
        val nextDue = DateTimeService.getNextDueDate(
            task.occurrence,
            task.repeat,
            task.lastCompleted,
            task.weeklyRecurrence
        )
        return nextDue.before(dateToday)
    }
}