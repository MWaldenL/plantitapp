package com.mobdeve.s15.group8.mobdeve_mp.model.services

import com.google.firebase.firestore.DocumentSnapshot
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F

object UserService {
    suspend fun getUserById(id: String): DocumentSnapshot? {
       return try {
           DBService.readDocument(
               collection=F.usersCollection,
               id)
       } catch (e: Exception) { null }
    }

    fun addUser(id: String) {
        val now = DateTimeService.getCurrentDateTime()
        DBService.addDocument( // create a new user document
            collection=F.usersCollection,
            id,
            data=hashMapOf(
                "name" to F.auth.currentUser!!.displayName,
                "dateJoined" to now,
                "feedbackStop" to false,
                "feedbackLastSent" to now,
                "pushAsked" to false
            ))
    }
}