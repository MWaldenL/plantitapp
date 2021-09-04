package com.mobdeve.s15.group8.mobdeve_mp.model.workers

import android.app.Notification.DEFAULT_ALL
import android.app.Notification.PRIORITY_MAX
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.MainActivity
import com.mobdeve.s15.group8.mobdeve_mp.singletons.PushPermissions
import java.util.*
import java.util.concurrent.TimeUnit

class DisablingNotificationWorker(context: Context, params: WorkerParameters):
    Worker(context, params)
{
    override fun doWork(): Result {
        val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()
        sendNotification(id)

        val pushStatus = applicationContext.getSharedPreferences(applicationContext.getString(R.string.SP_NAME), Context.MODE_PRIVATE)
                .getInt(applicationContext.getString(R.string.SP_PUSH_KEY), -1)

        if (pushStatus == PushPermissions.NOT_ALLOWED.ordinal)
            sendNotification(id)

        return Result.success()
    }

    private fun sendNotification(id: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID, id)

        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent = getActivity(applicationContext, 0, intent, 0)

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_plant_24)
            .setContentTitle("Bye Bye :<")
            .setContentText("You disabled notifications. This is the last one you'll receive unless you re-enable them in your profile.")
            .setStyle(NotificationCompat.BigTextStyle().bigText("You disabled notifications. This is the last one you'll receive unless you re-enable them in your profile."))
            .setDefaults(DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(PRIORITY_MAX)

        if (SDK_INT >= O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)

            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                NOTIFICATION_NAME,
                IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(id, notification.build())
    }

    companion object {
        const val NOTIFICATION_ID = "appName_notification_id3"
        const val NOTIFICATION_NAME = "appName3"
        const val NOTIFICATION_CHANNEL = "appName_channel_03"
    }
}
