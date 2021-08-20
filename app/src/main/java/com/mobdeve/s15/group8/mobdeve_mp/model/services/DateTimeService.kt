package com.mobdeve.s15.group8.mobdeve_mp.model.services

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
}