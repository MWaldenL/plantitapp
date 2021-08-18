package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.mobdeve.s15.group8.mobdeve_mp.F
import com.mobdeve.s15.group8.mobdeve_mp.GoogleSingleton
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.DashboardFragment
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var btnLogin: SignInButton
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSingleton.googleSigninOptions)
        btnLogin = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener { googleLauncher.launch(mGoogleSignInClient.signInIntent) }
    }

    private val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                mFirebaseAuthWithGoogle(account?.idToken!!)
            } catch (e: ApiException) {
                Log.e("TAG","signInResult:failed code=" + e.statusCode)
            }
        }
    }

    private val dashboardLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> }

    private fun mFirebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        F.auth
            .signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = F.auth.currentUser
                    val userId = user?.uid.toString()
                    val userDoc = F.usersCollection.document(userId)
                    val now: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'").format(Date())
                    userDoc.get().addOnSuccessListener { doc ->
                        if (doc.data == null) {
                            DBService.addDocument(
                                collection=F.usersCollection,
                                id=userId,
                                data=hashMapOf(
                                    "name" to user?.displayName,
                                    "dateJoined" to now,
                                    "plants" to ArrayList<String>()))
                        }
                    }
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                }
            }
    }

}