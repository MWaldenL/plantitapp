package com.mobdeve.s15.group8.mobdeve_mp.controller.services

import android.content.Context
import androidx.work.*
import com.mobdeve.s15.group8.mobdeve_mp.model.workers.NotificationWorker
import java.util.*
import java.util.concurrent.TimeUnit

object NotificationService {
    fun scheduleNotification(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 10)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)

        if (c.before(Calendar.getInstance()))
            c.add(Calendar.DATE, 1)

        val delay = c.timeInMillis - Calendar.getInstance().timeInMillis

        val dailyWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(5000, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueue(dailyWorkRequest)
    }
}