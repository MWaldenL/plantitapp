package com.mobdeve.s15.group8.mobdeve_mp.singletons

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object F {
    val usersCollection = Firebase.firestore.collection("users")
    val plantsCollection = Firebase.firestore.collection("plants")
    val tasksCollection = Firebase.firestore.collection("tasks")
    val auth: FirebaseAuth = Firebase.auth
}