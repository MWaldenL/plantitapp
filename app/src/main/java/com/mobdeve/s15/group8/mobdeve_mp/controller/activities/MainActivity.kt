package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.cloudinary.android.MediaManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mobdeve.s15.group8.mobdeve_mp.F
import com.mobdeve.s15.group8.mobdeve_mp.GoogleSingleton
//import com.cloudinary.android.MediaManager
import com.mobdeve.s15.group8.mobdeve_mp.R

class MainActivity: AppCompatActivity() {
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> }

    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MediaManager.init(this) // for Cloudinary

        bottomNav = findViewById(R.id.bottom_nav_view)
        val navController = findNavController(R.id.nav_fragment)
        bottomNav.setupWithNavController(navController)

        /*if (F.auth.currentUser == null) {
            launcher.launch(Intent(this@MainActivity, LoginActivity::class.java))
        } else {
            launcher.launch(Intent(this@MainActivity, DashboardActivity::class.java))
        }*/
    }
}
