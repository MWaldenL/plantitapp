package com.mobdeve.s15.group8.mobdeve_mp.model.services

import com.google.firebase.firestore.DocumentSnapshot
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object UserService: CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    suspend fun getUserById(id: String): DocumentSnapshot? {
       return try {
           DBService.readDocument(
               collection=F.usersCollection,
               id)
       } catch (e: Exception) { null }
    }

    fun addUser(id: String) {
        val now = DateTimeService.getCurrentDateTime()
        launch(coroutineContext) {
            DBService.addDocument(
                collection=F.usersCollection,
                id,
                data=hashMapOf(
                    "name" to F.auth.currentUser!!.displayName,
                    "dateJoined" to now
                ))
        }
    }
}