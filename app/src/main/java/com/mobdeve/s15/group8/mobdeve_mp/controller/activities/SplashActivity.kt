package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.mobdeve.s15.group8.mobdeve_mp.F
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.DBCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository

class SplashActivity : AppCompatActivity(), DBCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MediaManager.init(this) // initializer for Cloudinary's image upload
        if (F.auth.currentUser == null) { // no user - go to login
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        } else {
            PlantRepository.setOnDataFetchedListener(this) // Wait on the plant repository's fetching
            PlantRepository.getData() // The plant repository will inform us when they are done
        }
    }

    override fun onDataRetrieved(doc: MutableMap<String, Any>, id: String, type: String) {
    }

    override fun onComplete() { // Once the plant repo has informed us, go to MainActivity
        PlantRepository.setOnDataFetchedListener(null)
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}