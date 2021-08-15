package com.mobdeve.s15.group8.mobdeve_mp.model.repositories

import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
import com.mobdeve.s15.group8.mobdeve_mp.F
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import kotlinx.coroutines.tasks.await

object PlantRepository {
    val plantList: ArrayList<Plant> = ArrayList()

    private suspend fun mFetchData(): QuerySnapshot? {
        return try {
            val data = F.plantsCollection.get().await() // in the future, get a plant from user coll
            data
        } catch (e: Exception) {
            Log.d("DEBUG", "mFetchData:  $e")
            null
        }
    }

    suspend fun fetchData() {
        val res = mFetchData()
        if (res != null) {
            for (doc in res) {
                val d = doc.data
                val id = doc.id
                val journal = ArrayList<Journal>()
                val tasks = ArrayList<Task>()
                val imageUrl = d["imageUrl"].toString()
                val name = d["name"].toString()
                val nickname = d["nickname"].toString()
                val datePurchased = d["datePurchased"].toString()
                val docTasks = d["tasks"] as ArrayList<HashMap<*, *>>
                val docJournal = d["journal"] as ArrayList<HashMap<*, *>>
                for (t in docTasks) {
                    val action = t["action"].toString()
                    val occurrence = t["occurrence"].toString()
                    val repeat = t["repeat"].toString().toInt()
                    val startDate = t["startDate"].toString()
                    tasks.add(Task(action, startDate, repeat, occurrence))
                }
                for (j in docJournal) {
                    val body = j["body"].toString()
                    val date = j["date"].toString()
                    journal.add(Journal(body, date))
                }
                plantList.add(Plant(
                    id,
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
