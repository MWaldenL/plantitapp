package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cloudinary.android.MediaManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.cloudinary.android.MediaManager
import com.mobdeve.s15.group8.mobdeve_mp.R

class MainActivity: AppCompatActivity() {
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> }

    lateinit var bottomNav: BottomNavigationView
    lateinit var fabAddPlant: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        MediaManager.init(this) // for Cloudinary

        bottomNav = findViewById(R.id.bottom_nav_view)
        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navFragment.navController
        bottomNav.setupWithNavController(navController)

        fabAddPlant = findViewById(R.id.fab_add_plant)
        fabAddPlant.setOnClickListener {
            launcher.launch(Intent(this@MainActivity, AddPlantActivity::class.java))
        }

        /*if (F.auth.currentUser == null) {
            launcher.launch(Intent(this@MainActivity, LoginActivity::class.java))
        } else {
            launcher.launch(Intent(this@MainActivity, DashboardActivity::class.java))
        }*/
    }
}
