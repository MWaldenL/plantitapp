package com.mobdeve.s15.group8.mobdeve_mp.model.services

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.DBCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * The main database helper for the app. This helper includes the basic CRUD operations on firestore.
 * Note: after using a DBCallback listener that won't be used again, invoke setDBCallbackListener(null)
 * to prevent errors.
 */
object DBService: CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()
    lateinit var mListener: DBCallback

    fun setDBCallbackListener(listener: DBCallback) {
        mListener = listener
    }

    fun readDocument(collection: CollectionReference, id: String) {
        launch(coroutineContext) {
            collection.document(id).get().addOnSuccessListener { doc ->
                if (doc != null) {
                    doc.data?.let { mListener.onDataRetrieved(it, id, collection.id) }
                } else {
                    Log.d("DBService", "No doc")
                }
            }
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

    fun updateDocument(collection: CollectionReference, id: String?, value: Any) {
        if (id == null) return
        launch(Dispatchers.IO) {
            collection.document(id).update(value as MutableMap<String, Any>)
        }
    }

    fun deleteDocument(collection: CollectionReference, id: String?) {
        if (id == null) return
        launch(Dispatchers.IO) {
            collection.document(id).delete()
        }
    }
}