package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobdeve.s15.group8.mobdeve_mp.F
import com.mobdeve.s15.group8.mobdeve_mp.GoogleSingleton
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository

class HomeActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabAddPlant: FloatingActionButton
    private val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        PlantRepository.getData()
        bottomNav = findViewById(R.id.bottom_nav_view)
        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navFragment.navController
        val btnSignout = findViewById<Button>(R.id.btn_signout)
        bottomNav.setupWithNavController(navController)

        fabAddPlant = findViewById(R.id.fab_add_plant)
        fabAddPlant.setOnClickListener {
            val addPlantIntent = Intent(this@HomeActivity, AddPlantActivity::class.java)
            startActivity(addPlantIntent)
        }
        btnSignout.setOnClickListener {
            F.auth.signOut()
            GoogleSignIn.getClient(this, GoogleSingleton.googleSigninOptions).signOut()
            loginLauncher.launch(Intent(this@HomeActivity, LoginActivity::class.java))
            finish()
        }
    }
}