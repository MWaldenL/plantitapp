package com.mobdeve.s15.group8.mobdeve_mp.controller

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.GoogleSingleton

class MainActivity : AppCompatActivity() {
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launcher.launch(Intent(this@MainActivity, ViewAllPlantsActivity::class.java)) // temporary
        MediaManager.init(this)
//        if (GoogleSingleton.firebaseAuth.currentUser == null) {
//            launcher.launch(Intent(this@MainActivity, LoginActivity::class.java))
//        } else {
//            launcher.launch(Intent(this@MainActivity, DashboardActivity::class.java))
//        }
    }
}
