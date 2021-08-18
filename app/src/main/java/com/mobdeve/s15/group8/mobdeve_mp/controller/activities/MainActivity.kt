package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cloudinary.android.MediaManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobdeve.s15.group8.mobdeve_mp.F
import com.mobdeve.s15.group8.mobdeve_mp.GoogleSingleton
import com.mobdeve.s15.group8.mobdeve_mp.R

class MainActivity: AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabAddPlant: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav_view)
        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navFragment.navController
        val btnSignout = findViewById<Button>(R.id.btn_signout)
        bottomNav.setupWithNavController(navController)

        fabAddPlant = findViewById(R.id.fab_add_plant)
        fabAddPlant.setOnClickListener {
            val addPlantIntent = Intent(this@MainActivity, AddPlantActivity::class.java)
            startActivity(addPlantIntent)
        }
        btnSignout.setOnClickListener {
            Log.d("WTF", "wtf")
            F.auth.signOut()
            GoogleSignIn.getClient(this, GoogleSingleton.googleSigninOptions).signOut()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }
}
