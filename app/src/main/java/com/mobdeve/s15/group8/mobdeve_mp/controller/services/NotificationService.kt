package com.mobdeve.s15.group8.mobdeve_mp.controller.services

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mobdeve.s15.group8.mobdeve_mp.model.services.TaskService
import com.mobdeve.s15.group8.mobdeve_mp.model.workers.CompleteNotificationWorker
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

    fun sendCompleteNotification(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val dailyWorkRequest = OneTimeWorkRequestBuilder<CompleteNotificationWorker>()
            .setInitialDelay(30, TimeUnit.MINUTES)
            .build()

        workManager.enqueue(dailyWorkRequest)
    }
}