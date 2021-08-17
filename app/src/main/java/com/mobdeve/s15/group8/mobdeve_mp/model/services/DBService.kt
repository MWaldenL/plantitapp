package com.mobdeve.s15.group8.mobdeve_mp.model.services

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.DBCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

object DBService: CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()
    lateinit var mListener: DBCallback

    fun setDBCallbackListener(listener: PlantRepository) {
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
}