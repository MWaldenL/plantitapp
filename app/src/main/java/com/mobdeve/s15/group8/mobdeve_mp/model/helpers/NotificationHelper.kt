package com.mobdeve.s15.group8.mobdeve_mp.model.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.MainActivity
import com.mobdeve.s15.group8.mobdeve_mp.model.services.TaskService

class NotificationHelper(base: Context?): ContextWrapper(base) {
    private val channelID: String = "channelID"
    private val channelName: String = "Channel Name"

    private val mManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun getManager(): NotificationManager {
        return mManager
    }

    // TODO: Edit this to get daily tasks or congratulations
    fun getNotification(): NotificationCompat.Builder {
        val tasks = TaskService.getTasksToday(includeFinished = false)
        val title: String
        val content: String

        if (tasks.isEmpty()) {
            title = "Hooray!"
            content = "Your plants are taken care of for today. You can relax!"
        } else {
            title = "Your plants are calling..."
            content = "Your plants need you to take care of them. Open PlantitApp to see what you need to do."
        }

        val intent = Intent(this, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)


        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSmallIcon(R.drawable.ic_plant_24)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(): NotificationChannel {
        return NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
    }
}