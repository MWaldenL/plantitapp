package com.mobdeve.s15.group8.mobdeve_mp.model.repositories

import com.mobdeve.s15.group8.mobdeve_mp.F
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.DBCallback
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.RefreshCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService

object PlantRepository: DBCallback {
    private var mDBListener: DBCallback? = null
    private var mRefreshListener: RefreshCallback? = null
    var plantList: ArrayList<Plant> = ArrayList()
    private var mUserDoc: MutableMap<String, Any> = HashMap()
    private var mPlantDoc: MutableMap<String, Any> = HashMap()

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
        plantList = ArrayList()
        F.auth.currentUser?.uid?.let {
            DBService.readDocument(
                collection=F.usersCollection,
                id=it)
        }
    }

    override fun onDataRetrieved(doc: MutableMap<String, Any>, id: String, type: String) {
        // DBService will inform us of what kind of data was retrieved - either user or plant
        if (type == "users") { // Fetch the user's plants
            val plants = doc["plants"] as ArrayList<String>

            // Create a plant object for each id
            for (plantId in plants) {
                DBService.readDocument(
                    collection=F.plantsCollection,
                    id=plantId)
            }

            // Notify listeners on data fetch completion
            if (mDBListener != null) {
                mDBListener?.onComplete()
            }
            mRefreshListener?.onRefreshSuccess()
        } else { // Fetch the plant and add to plant list
            val journal = ArrayList<Journal>()
            val tasks = ArrayList<Task>()
            val docTasks = doc["tasks"] as ArrayList<HashMap<*, *>>
            val docJournal = doc["journal"] as ArrayList<HashMap<*, *>>
            for (t in docTasks) {
                tasks.add(Task(
                    action=t["action"].toString(),
                    startDate=t["startDate"].toString(),
                    repeat=t["repeat"].toString().toInt(),
                    occurrence=t["occurrence"].toString()))
            }
            for (j in docJournal) {
                journal.add(Journal(
                    body=j["body"].toString(),
                    date=j["date"].toString()))
            }
            plantList.add(Plant(
                id,
                imageUrl=doc["imageUrl"].toString(),
                filePath="",
                name=doc["name"].toString(),
                nickname=doc["nickname"].toString(),
                dateAdded=doc["dateAdded"].toString(),
                death=doc["death"] as Boolean,
                tasks,
                journal
            ))
        }
    }

    override fun onComplete() {
    }
}
