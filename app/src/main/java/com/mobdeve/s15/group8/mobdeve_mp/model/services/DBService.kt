package com.mobdeve.s15.group8.mobdeve_mp.model.services

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

object DBService: CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    suspend fun readCollection(collection: CollectionReference): QuerySnapshot? {
        return try {
            collection.get().await()
        } catch(e: Exception) {
            Log.e("DBService readCol", "$e")
            null
        }
    }

    suspend fun readDocument(collection: CollectionReference, id: String): DocumentSnapshot? {
        return try {
            collection.document(id).get().await()
        } catch (e: Exception) {
            Log.e("DBService", "$e")
            null
        }
    }

    fun addDocument(collection: CollectionReference, id: String="", data: Any) {
        launch(Dispatchers.IO) {
            when (id) {
                "" -> collection.add(data)
                else -> collection.document(id).set(data)
            }
        }
    }

    fun updateDocument(collection: CollectionReference, id: String?, field: String, value: Any) {
        if (id == null) return
        launch(Dispatchers.IO) {
            collection.document(id).update(field, value)
        }
    }

    fun deleteDocument(collection: CollectionReference, id: String?) {
        if (id == null) return
        launch(Dispatchers.IO) {
            collection.document(id).delete()
        }
    }
}