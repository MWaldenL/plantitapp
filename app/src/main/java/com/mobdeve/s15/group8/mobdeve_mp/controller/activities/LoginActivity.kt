package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import com.mobdeve.s15.group8.mobdeve_mp.singletons.GoogleSingleton
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.DBCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class LoginActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var btnLogin: SignInButton
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSingleton.googleSigninOptions)
        btnLogin = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener { googleLauncher.launch(mGoogleSignInClient.signInIntent) }
    }

    private val googleLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) { // authenticate with Google
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try { // then with firebase
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                mFirebaseAuthWithGoogle(account?.idToken!!)
            } catch (e: ApiException) {
                Log.e("TAG","signInResult:failed code=" + e.statusCode)
            }
        }
    }

    private fun mFirebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        F.auth // sign the user in with their google account
            .signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { // get the completed user object
                    val userId = F.auth.currentUser!!.uid
                    launch(coroutineContext) {
                        val doc = UserService.getUserById(userId)
                        if (doc?.data == null) {
                            UserService.addUser(userId)
                        }
                    }
                    startActivity(Intent(this@LoginActivity, SplashActivity::class.java))
                    finish()
                }
            }
    }
}