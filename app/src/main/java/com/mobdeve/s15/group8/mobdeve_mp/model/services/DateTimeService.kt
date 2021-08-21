package com.mobdeve.s15.group8.mobdeve_mp.model.services

import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateTimeService {

    fun getCurrentDate(): String {
        return SimpleDateFormat.getDateInstance(DateFormat.LONG).format(Date())
    }

    fun getCurrentTime(): String {
        return SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(Date())
    }

    fun getCurrentDateTime(): String {
        return SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(Date())
    }

    fun getCurrentDateWithoutTime(): Calendar {
        val cal = Calendar.getInstance()
        removeCalendarTime(cal)
        return cal
    }

    fun removeCalendarTime(cal: Calendar) {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
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