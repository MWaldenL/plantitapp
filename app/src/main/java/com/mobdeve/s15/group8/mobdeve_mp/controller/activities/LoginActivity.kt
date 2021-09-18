package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
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
import com.mobdeve.s15.group8.mobdeve_mp.model.services.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class LoginActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var btnLogin: SignInButton
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mEditor: SharedPreferences.Editor

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSingleton.googleSigninOptions)
        btnLogin = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener { googleLauncher.launch(mGoogleSignInClient.signInIntent) }

        mSharedPreferences = this.getSharedPreferences(getString(R.string.SP_NAME), Context.MODE_PRIVATE)
        mEditor = mSharedPreferences.edit()
    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish()
//        }
//        return true
//    }

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

                            mEditor.putInt(
                                getString(R.string.SP_FEED_TIME_KEY),
                                TimeUnit.HOURS.convert(Date().time, TimeUnit.MILLISECONDS).toInt()
                            )
                            mEditor.apply()
                        }
                    }
                    startActivity(Intent(this@LoginActivity, SplashActivity::class.java))
                    finish()
                }
            }
    }
}