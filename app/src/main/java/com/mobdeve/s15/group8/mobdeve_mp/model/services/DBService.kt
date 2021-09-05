package com.mobdeve.s15.group8.mobdeve_mp.model.services

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

/**
 * The main database helper for the app. This helper includes the basic CRUD operations on firestore.
 * Note: after using a DBCallback listener that won't be used again, invoke setDBCallbackListener(null)
 * to prevent errors.
 */
object DBService: CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    suspend fun readDocument(collection: CollectionReference, id: String): DocumentSnapshot? {
        return try {
            collection.document(id).get().await()
        } catch (e: Exception) {
            Log.e("DBService", "$e")
            null
        }
    }

    suspend fun readDocuments(collection: CollectionReference, where: String, equalTo: Any): QuerySnapshot? {
        return try {
            collection.whereEqualTo(where, equalTo).get().await()
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

    fun updateDocument(collection: CollectionReference, id: String?, fieldValues: HashMap<String, Any>) {
        if (id == null) return
        launch(Dispatchers.IO) {
            collection.document(id).update(fieldValues)
        }
    }

    fun deleteDocument(collection: CollectionReference, id: String?) {
        if (id == null) return
        launch(Dispatchers.IO) {
            collection.document(id).delete()
        }
    }
}