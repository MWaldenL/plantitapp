package com.mobdeve.s15.group8.mobdeve_mp.controller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.GoogleSingleton

class MainActivity : AppCompatActivity() {
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (GoogleSingleton.firebaseAuth.currentUser == null) {
            Log.d("hatdog", "hello world")
            launcher.launch(Intent(this@MainActivity, LoginActivity::class.java))
        } else {
            launcher.launch(Intent(this@MainActivity, DashboardActivity::class.java))
        }
    }
}