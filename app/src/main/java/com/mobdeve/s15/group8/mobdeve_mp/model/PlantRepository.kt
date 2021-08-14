package com.mobdeve.s15.group8.mobdeve_mp.model

import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
import com.mobdeve.s15.group8.mobdeve_mp.F
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat

class PlantRepository {
    val plantList: ArrayList<Plant> = ArrayList()

    private suspend fun mFetchData(): QuerySnapshot? {
        return try {
            val data = F.plantsCollection.get().await()
            data
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getData() {
        val res = mFetchData()
        if (res != null) {
            for (doc in res) {
                val d = doc.data
                val journal = ArrayList<Journal>()
                val tasks = ArrayList<Task>()
                val imageUrl = d["imageUrl"].toString()
                val name = d["name"].toString()
                val nickname = d["nickname"].toString()
                val datePurchased = d["datePurchased"].toString()
                val docTasks = d["tasks"] as ArrayList<HashMap<*, *>>
                val docJournal = d["journal"] as ArrayList<HashMap<*, *>>

                for (t in docTasks) {
                    val done = t["done"].toString().toBoolean()
                    val task = t["task"].toString()
                    val everyTime = t["everyTime"].toString().toInt()
                    tasks.add(Task(done, task, everyTime))
                }
                for (j in docJournal) {
                    val body = j["body"].toString()
                    val date = j["date"].toString()
                    journal.add(Journal(body, date))
                }
                plantList.add(Plant(
                    imageUrl,
                    name,
                    nickname,
                    datePurchased,
                    tasks,
                    journal))
            }
        }
    }
}
