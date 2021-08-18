package com.mobdeve.s15.group8.mobdeve_mp.model.services

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

}