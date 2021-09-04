package com.mobdeve.s15.group8.mobdeve_mp.controller.services

import android.content.Context
import androidx.work.*
import com.mobdeve.s15.group8.mobdeve_mp.model.workers.FirstNotificationWorker
import java.util.concurrent.TimeUnit

object NotificationService {
    fun scheduleNotification(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val dailyWorkRequest = OneTimeWorkRequestBuilder<FirstNotificationWorker>()
            .setInitialDelay(3000, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueue(dailyWorkRequest)
    }
}