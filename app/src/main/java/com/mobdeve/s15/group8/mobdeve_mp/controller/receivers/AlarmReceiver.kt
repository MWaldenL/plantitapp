package com.mobdeve.s15.group8.mobdeve_mp.controller.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.mobdeve.s15.group8.mobdeve_mp.model.services.NotificationHelper

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val helper = NotificationHelper(context)
        val manager = helper.getManager()
        val builder = helper.getNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            manager.createNotificationChannel(helper.createChannel())

        manager.notify(1, builder.build())
    }
}