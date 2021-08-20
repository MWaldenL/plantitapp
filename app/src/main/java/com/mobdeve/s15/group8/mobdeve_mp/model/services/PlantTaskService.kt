package com.mobdeve.s15.group8.mobdeve_mp.model.services

import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import java.util.*

object PlantTaskService {
    fun findPlantById(id: String): Plant? {
        for (plant in PlantRepository.plantList) {
            if (plant.id == id)
                return plant
        }
        return null
    }

    fun findPlantByName(name: String): Plant? {
        for (plant in PlantRepository.plantList) {
            if (plant.name == name)
                return plant
        }
        return null
    }

    fun findTaskByAction(action: String, plant: Plant): Task? {
        for (task in plant.tasks)
            if (task.action == action)
                return task
        return null
    }

    fun getNextDueDate(task: Task, prevDate: Date): Calendar {
        val cal = Calendar.getInstance()
        cal.time = prevDate
        DateTimeService.removeCalendarTime(cal)
        when (task.occurrence) {
            "Day" ->
                cal.add(Calendar.DATE, task.repeat)
            "Week" ->
                cal.add(Calendar.DATE, task.repeat * 7)
            "Month" ->
                cal.add(Calendar.MONTH, task.repeat)
            "Year" ->
                cal.add(Calendar.YEAR, task.repeat)
        }
        return cal
    }

    fun getLastDueDate(task: Task, currDate: Date): Calendar {
        val cal = Calendar.getInstance()
        cal.time = currDate
        DateTimeService.removeCalendarTime(cal)
        when (task.occurrence) {
            "Day" ->
                cal.add(Calendar.DATE, -task.repeat)
            "Week" ->
                cal.add(Calendar.DATE, -task.repeat * 7)
            "Month" ->
                cal.add(Calendar.MONTH, -task.repeat)
            "Year" ->
                cal.add(Calendar.YEAR, -task.repeat)
        }
        return cal
    }
}