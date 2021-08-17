package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.mobdeve.s15.group8.mobdeve_mp.F
import com.mobdeve.s15.group8.mobdeve_mp.R

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MediaManager.init(this) // for Cloudinary
        if (F.auth.currentUser == null) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        } else {
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
        }
    }
}
