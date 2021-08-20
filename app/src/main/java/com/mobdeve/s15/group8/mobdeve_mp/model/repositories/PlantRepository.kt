package com.mobdeve.s15.group8.mobdeve_mp.model.repositories

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

    private var mDBListener: DBCallback? = null
    private var mRefreshListener: RefreshCallback? = null
    var plantList: ArrayList<Plant> = ArrayList()
    var tasksToday: HashMap<String, ArrayList<Task>> = HashMap()

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
                collection= F.usersCollection,
                id=it)
        }
    }

    override fun onDataRetrieved(doc: MutableMap<String, Any>, id: String, type: String) {
        // DBService will inform us of what kind of data was retrieved - either user or plant
        if (type == USERS_TYPE) { // Fetch the user's plants
            val plants = doc["plants"] as ArrayList<String>

            // Create a plant object for each id
            for (plantId in plants) {
                DBService.readDocument(
                    collection= F.plantsCollection,
                    id=plantId)
            }

            // Notify listeners on data fetch completion
            if (mDBListener != null)
                mDBListener?.onComplete(type)
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
                    occurrence=t["occurrence"].toString(),
                    lastCompleted = (t["lastCompleted"] as Timestamp).toDate(),
                ))
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

            // update the list of tasks for today
            mUpdateTasksToday()

            if (mDBListener != null)
                mDBListener?.onComplete(type)
        }
    }

    override fun onComplete(tag: String) {
    }

    private fun mUpdateTasksToday() {
        tasksToday = HashMap()

        val dateToday = DateTimeService.getCurrentDateWithoutTime()
        for (plant in plantList) {
            for (task in plant.tasks) {
                val nextDue = mGetNextDue(task, task.lastCompleted)
                if (!dateToday.before(nextDue)) {
                    if (tasksToday[plant.id] == null)
                        tasksToday[plant.id] = ArrayList()
                    tasksToday[plant.id]?.add(task)
                }
            }
        }
    }

    private fun mGetNextDue(task: Task, prevDate: Date): Calendar {
        val cal = Calendar.getInstance()
        cal.time = prevDate
        DateTimeService.removeCalendarTime(cal)
        when (task.occurrence) {
            "Day" ->
                cal.add(Calendar.DATE, task.repeat)
            "Week" ->
                cal.add(Calendar.DATE, task.repeat * 7)
            "Month" ->
                cal.add(Calendar.MONTH, task.repeat)
            "Year" ->
                cal.add(Calendar.YEAR, task.repeat)
        }
        return cal
    }
}
