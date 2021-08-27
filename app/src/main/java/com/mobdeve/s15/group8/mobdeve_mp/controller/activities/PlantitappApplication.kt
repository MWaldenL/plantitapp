package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.app.Application
import android.util.Log
import com.cloudinary.android.MediaManager

class PlantitappApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MediaManager.init(this)
    }
}