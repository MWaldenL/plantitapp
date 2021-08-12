package com.mobdeve.s15.group8.mobdeve_mp.controller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.F
import com.mobdeve.s15.group8.mobdeve_mp.GoogleSingleton

class LoginActivity : AppCompatActivity() {
    private lateinit var btnLogin: SignInButton
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account?.idToken!!)
            } catch (e: ApiException) {
                Log.e("TAG","signInResult:failed code=" + e.statusCode)
            }
        }
    }

    private val dashboardLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        GoogleSingleton.firebaseAuth
            .signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = GoogleSingleton.firebaseAuth.currentUser
                    val userId = user?.uid.toString()
                    val userDoc = F.usersCollection.document(userId)
                    userDoc.get().addOnSuccessListener { doc ->
                        if (doc.data == null) {
                            F.usersCollection.document(userId).set(hashMapOf(
                                "name" to user?.displayName
                            ))
                        }
                    }
                    val dashboardIntent = Intent(this@LoginActivity, DashboardActivity::class.java)
                    dashboardLauncher.launch(dashboardIntent)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSingleton.googleSigninOptions)
        btnLogin = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener { googleLauncher.launch(mGoogleSignInClient.signInIntent) }
    }
}