package com.mobdeve.s15.group8.mobdeve_mp.model

import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
import com.mobdeve.s15.group8.mobdeve_mp.F
import kotlinx.coroutines.tasks.await

class PlantRepository {
    val plantList: ArrayList<Plant> = ArrayList()

    private suspend fun mFetchData(): QuerySnapshot? {
        return try {
            Log.d("DEBUG", "mFetchData: Before fetching plants")
            val data = F.usersCollection.get().await()
            Log.d("DEBUG", "mFetchData: After fetching")
            data
        } catch (e: Exception) {
            Log.d("DEBUG", "mFetchData:  $e")
            null
        }
    }

    suspend fun getData() {
        Log.d("DEBUG", "Enter getData")
        val res = mFetchData()

        Log.d("DEBUG", "After fetchdata")
        if (res != null) {
            for (doc in res) {
                val d = doc.data
                val journal = ArrayList<Journal>()
                val tasks = ArrayList<Task>()
                Log.d("DEBUG", "Before declare")

                val imageUrl = d["imageUrl"].toString()
                val name = d["name"].toString()
                val nickname = d["nickname"].toString()
                val datePurchased = d["datePurchased"].toString()


                Log.d("DEBUG", "before tasks doc")
                val docTasks = d["tasks"] as ArrayList<HashMap<*, *>>

                val docJournal = d["journal"] as ArrayList<HashMap<*, *>>

                Log.d("DEBUG", "before tasks")
                for (t in docTasks) {
                    Log.d("DEBUG", "before action")
                    val action = t["action"].toString()

                    Log.d("DEBUG", "before occ")
                    val occurrence = t["occurrence"].toString()

                    Log.d("DEBUG", "before repeat")
                    val repeat = t["repeat"].toString().toInt()

                    Log.d("DEBUG", "before startD")
                    val startDate = t["startDate"].toString()

                    tasks.add(Task(action, startDate, repeat, occurrence))
                }
                Log.d("DEBUG", "after tasks")

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
