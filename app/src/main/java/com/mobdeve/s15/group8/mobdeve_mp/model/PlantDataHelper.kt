package com.mobdeve.s15.group8.mobdeve_mp.model

import com.google.firebase.firestore.QuerySnapshot
import com.mobdeve.s15.group8.mobdeve_mp.FirebaseSingleton
import kotlinx.coroutines.tasks.await

class PlantDataHelper {
    private suspend fun fetchData(): QuerySnapshot? {
        return try {
            val data = FirebaseSingleton.plantsCollection.get().await()
            data
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getData(): ArrayList<Plant> {
        val plantList: ArrayList<Plant> = ArrayList()
        val res = fetchData()
        if (res != null) {
            for (doc in res) {
                plantList.add(Plant(
                    doc.data["name"].toString(),
                    doc.data["nickname"].toString(),
                    doc.data["imageUrl"].toString()))
            }
        }
        return plantList
    }
}
