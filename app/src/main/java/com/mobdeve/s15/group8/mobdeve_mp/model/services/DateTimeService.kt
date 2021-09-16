package com.mobdeve.s15.group8.mobdeve_mp.model.services

import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object DateTimeService {

    fun getCurrentDate(): String {
        return SimpleDateFormat.getDateInstance(DateFormat.LONG).format(Date())
    }

    fun getCurrentDateCal(): Calendar {
        return Calendar.getInstance()
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

    fun getNextDueDate(taskOccurrence: String,
                       taskRepeat: Int,
                       date: Date,
                       weeklyRecurrence: ArrayList<Int>? = null): Calendar {

        val nextDue = Calendar.getInstance()
        nextDue.time = date
        removeCalendarTime(nextDue)
        when (taskOccurrence) {
            "Day" ->
                nextDue.add(Calendar.DATE, taskRepeat)
            "Week" -> {
                // get current day of week (1 = Sunday, and so on)
                val currCal = Calendar.getInstance()
                currCal.time = date
                val currDayOfWeek = currCal.get(Calendar.DAY_OF_WEEK)

                // Calculate the num of days before next due
                var minDays = 7
                if (weeklyRecurrence != null) {
                    for (dayOfWeek: Int in weeklyRecurrence) {
                        var days = dayOfWeek + 7 - currDayOfWeek
                        if (days > 7) days %= 7
                        if (days < minDays) minDays = days
                    }
                }
                nextDue.add(Calendar.DATE, minDays)
            }
            "Month" ->
                nextDue.add(Calendar.MONTH, taskRepeat)
            "Year" ->
                nextDue.add(Calendar.YEAR, taskRepeat)
        }
        return nextDue
    }

    fun getLastDueDate(taskOccurrence: String,
                       taskRepeat: Int,
                       currDate: Date,
                       weeklyRecurrence: ArrayList<Int>? = null): Calendar {
        val cal = Calendar.getInstance()
        cal.time = currDate
        removeCalendarTime(cal)
        when (taskOccurrence) {
            "Day" ->
                cal.add(Calendar.DATE, -taskRepeat)
            "Week" -> {
                Log.d("RECURRENCE", "weekly daw")
                // get current day of week (1 = Sunday, and so on)
                val currCal = Calendar.getInstance()
                currCal.time = currDate
                val currDayOfWeek = currCal.get(Calendar.DAY_OF_WEEK)

                // Calculate the num of days before next due
                var maxDays = -7
                if (weeklyRecurrence != null) {
                    for (dayOfWeek: Int in weeklyRecurrence) {
                        var days = dayOfWeek - currDayOfWeek
                        if (days >= 0) days -= 7
                        if (days > maxDays) maxDays = days
                    }
                }

                cal.add(Calendar.DATE, maxDays)
                Log.d("RECURRENCE", "result: ${cal}")
            }
            "Month" ->
                cal.add(Calendar.MONTH, -taskRepeat)
            "Year" ->
                cal.add(Calendar.YEAR, -taskRepeat)
        }
        return cal
    }
    
    fun stringToDate(string: String): Date {
        return SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).parse(string) as Date
    }

}