package com.mobdeve.s15.group8.mobdeve_mp.model.services

import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
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

    fun getNextDueDate(taskOccurrence: String, taskRepeat: Int, date: Date): Calendar {
        val nextDue = Calendar.getInstance()
        nextDue.time = date
        removeCalendarTime(nextDue)
        when (taskOccurrence) {
            "Day" ->
                nextDue.add(Calendar.DATE, taskRepeat)
            "Week" ->
                nextDue.add(Calendar.DATE, taskRepeat * 7)
            "Month" ->
                nextDue.add(Calendar.MONTH, taskRepeat)
            "Year" ->
                nextDue.add(Calendar.YEAR, taskRepeat)
        }
        return nextDue
    }

    fun getLastDueDate(taskOccurrence: String, taskRepeat: Int, currDate: Date): Calendar {
        val cal = Calendar.getInstance()
        cal.time = currDate
        removeCalendarTime(cal)
        when (taskOccurrence) {
            "Day" ->
                cal.add(Calendar.DATE, -taskRepeat)
            "Week" ->
                cal.add(Calendar.DATE, -taskRepeat * 7)
            "Month" ->
                cal.add(Calendar.MONTH, -taskRepeat)
            "Year" ->
                cal.add(Calendar.YEAR, -taskRepeat)
        }
        return cal
    }
}