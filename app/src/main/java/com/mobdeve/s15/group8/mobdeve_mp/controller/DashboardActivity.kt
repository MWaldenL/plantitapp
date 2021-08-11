package com.mobdeve.s15.group8.mobdeve_mp.controller

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.GoogleSingleton

class DashboardActivity : AppCompatActivity() {
    private lateinit var textGreeting: TextView
    private lateinit var buttonSignOut: Button
    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        textGreeting = findViewById(R.id.text_user)
        textGreeting.text = GoogleSingleton.firebaseAuth.currentUser?.displayName
        buttonSignOut = findViewById(R.id.button_signout)
        buttonSignOut.setOnClickListener {
            GoogleSingleton.firebaseAuth.signOut()
            GoogleSignIn.getClient(this, GoogleSingleton.googleSigninOptions).signOut()
            val loginIntent = Intent(this@DashboardActivity, MainActivity::class.java)
            loginLauncher.launch(loginIntent)
        }
    }
}