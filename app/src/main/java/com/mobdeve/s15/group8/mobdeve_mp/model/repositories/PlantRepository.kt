package com.mobdeve.s15.group8.mobdeve_mp.model.repositories

import android.util.Log
import com.google.firebase.Timestamp
import com.google.type.DateTime
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.DBCallback
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.RefreshCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.PlantService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.TaskService
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * The plant data source for the app. This is called on startup to fetch all of a user's plants into
 * memory for viewing and interacting. Call getData to make a fetch request to Firebase
 */
object PlantRepository: DBCallback {
    const val USERS_TYPE = "users"
    const val PLANTS_TYPE = "plants"
    const val TASKS_TYPE = "tasks"

    private var mDBListener: DBCallback? = null
    private var mRefreshListener: RefreshCallback? = null
    var plantList: ArrayList<Plant> = ArrayList()
    var taskList: ArrayList<Task> = ArrayList()

    init {
        DBService.setDBCallbackListener(this)
    }

    fun setOnDataFetchedListener(listener: DBCallback?) {
        mDBListener = listener
    }

    fun setRefreshedListener(listener: RefreshCallback) {
        mRefreshListener = listener
    }

    fun getData() {
        F.auth.currentUser?.uid?.let {
            DBService.readDocument(
                collection = F.usersCollection,
                id = it)
        }
    }

    override fun onDataRetrieved(doc: MutableMap<String, Any>, id: String, type: String) {
        // DBService will inform us of what kind of data was retrieved - either user or plant
        if (type == USERS_TYPE) { // Fetch the user's plants
            // Notify listeners on user fetch completion
            if (mDBListener != null)
                mDBListener?.onComplete(type)

            // Create a plant object for each id
            val plants = doc["plants"] as ArrayList<String>

            // Fetch plants
            DBService.readDocuments(
                collection = F.plantsCollection,
                where = "userId",
                equalTo = F.auth.uid!!
            )

            // Fetch tasks
            DBService.readDocuments(
                collection = F.tasksCollection,
                where = "userId",
                equalTo = F.auth.uid!!
            )

            mRefreshListener?.onRefreshSuccess()
        }
    }

    override fun onDataRetrieved(docs: ArrayList<MutableMap<String, Any>>, type: String) {
        if (type == TASKS_TYPE) {
            taskList = ArrayList()
            for (doc in docs) {
                taskList.add(Task(
                    id = doc["id"].toString(),
                    plantId = doc["plantId"].toString(),
                    userId = doc["userId"].toString(),
                    action = doc["action"].toString(),
                    startDate = doc["startDate"].toString(),
                    repeat = doc["repeat"].toString().toInt(),
                    occurrence = doc["occurrence"].toString(),
                    lastCompleted = (doc["lastCompleted"] as Timestamp).toDate()
                ))
            }
            if (mDBListener != null)
                mDBListener?.onComplete(type)

        } else if (type == PLANTS_TYPE) {
            plantList = ArrayList()
            for (doc in docs) {
                val newPlant = Plant(
                    id = doc["id"].toString(),
                    userId = doc["userId"].toString(),
                    imageUrl = doc["imageUrl"].toString(),
                    filePath = "",
                    name = doc["name"].toString(),
                    nickname = doc["nickname"].toString(),
                    dateAdded = doc["dateAdded"].toString(),
                    death = doc["death"] as Boolean,
                    tasks = ArrayList(),
                    journal = ArrayList()
                )
                for (task in doc["tasks"] as ArrayList<String>)
                    newPlant.tasks.add(task)
                for (journalEntry in (doc["journal"] as ArrayList<HashMap<*, *>>))
                    newPlant.journal.add(Journal(
                        body = journalEntry["body"].toString(),
                        date = journalEntry["date"].toString()
                    ))
                plantList.add(newPlant)
            }
            if (mDBListener != null)
                mDBListener?.onComplete(type)
        }
    }

    override fun onComplete(tag: String) {
    }
}
