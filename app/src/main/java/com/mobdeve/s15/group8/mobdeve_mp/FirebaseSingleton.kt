package com.mobdeve.s15.group8.mobdeve_mp

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseSingleton {
    val usersCollection = Firebase.firestore.collection("users")
}