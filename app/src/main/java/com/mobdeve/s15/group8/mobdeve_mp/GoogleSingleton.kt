package com.mobdeve.s15.group8.mobdeve_mp

import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object GoogleSingleton {
    val googleSigninOptions: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("280609903358-d11ev4c65r15ghdp78pvi267a53ltl7k.apps.googleusercontent.com")
            .requestEmail()
            .build()
    val firebaseAuth: FirebaseAuth = Firebase.auth
}