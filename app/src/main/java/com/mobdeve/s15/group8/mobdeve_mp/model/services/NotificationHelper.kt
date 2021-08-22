package com.mobdeve.s15.group8.mobdeve_mp.model.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.mobdeve.s15.group8.mobdeve_mp.R

class NotificationHelper(base: Context?): ContextWrapper(base) {
    private val channelID: String = "channelID"
    private val channelName: String = "Channel Name"

    private val mManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun getManager(): NotificationManager {
        return mManager
    }

    // TODO: Edit this to get daily tasks or congratulations
    fun getNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle("Your plants are calling...")
            .setContentText("You have daily tasks that you need to do. Open PlantitApp to see them.")
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSmallIcon(R.drawable.ic_plant_24)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(): NotificationChannel {
        return NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
    }
}