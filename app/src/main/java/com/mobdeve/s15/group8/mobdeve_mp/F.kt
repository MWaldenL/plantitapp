package com.mobdeve.s15.group8.mobdeve_mp

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object F {
    val usersCollection = Firebase.firestore.collection("users")
    val plantsCollection = Firebase.firestore.collection("plants")
}