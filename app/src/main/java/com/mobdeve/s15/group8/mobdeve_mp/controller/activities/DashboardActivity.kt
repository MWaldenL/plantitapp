package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.mobdeve.s15.group8.mobdeve_mp.F
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.GoogleSingleton

class DashboardActivity : AppCompatActivity() {
    private lateinit var textGreeting: TextView
    private lateinit var buttonSignOut: Button
    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        textGreeting = findViewById(R.id.text_user)
        textGreeting.text = F.auth.currentUser?.displayName
        buttonSignOut = findViewById(R.id.btn_signout)
        buttonSignOut.setOnClickListener { mSignOut() }
    }

    private fun mSignOut() {
        F.auth.signOut()
        GoogleSignIn.getClient(this, GoogleSingleton.googleSigninOptions).signOut()
        loginLauncher.launch(Intent(this@DashboardActivity, MainActivity::class.java))
    }
}