package com.mobdeve.s15.group8.mobdeve_mp.model.repositories

import android.util.Log
import com.google.firebase.Timestamp
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.NewPlantCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Plant instance builder for new plants
 * Used by AddPlantActivity and TaskDialog
 * Builds a Hashmap<String, Any> to write to firestore
 */
object NewPlantInstance {
    var plant: HashMap<String, Any> = HashMap()
    var tasks: ArrayList<HashMap<String, Any>> = ArrayList()
    val plantObject: Plant
        get() {
            return Plant(
                plant["id"].toString(),
                plant["userId"].toString(),
                plant["imageUrl"].toString(),
                plant["filePath"].toString(),
                plant["name"].toString(),
                plant["nickname"].toString(),
                plant["dateAdded"].toString(),
                plant["death"] as Boolean,
                plant["tasks"] as ArrayList<String>,
                plant["journal"] as ArrayList<Journal>)
        }
    val tasksObject: ArrayList<Task>
        get() {
            val tasksObject: ArrayList<Task> = ArrayList()
            for (task in tasks)
                tasksObject.add(Task(
                    task["id"].toString(),
                    task["plantId"].toString(),
                    task["userId"].toString(),
                    task["action"].toString(),
                    task["startDate"].toString(),
                    task["repeat"] as Int,
                    task["occurrence"].toString(),
                    (task["lastCompleted"] as Timestamp).toDate()
                ))
            return tasksObject
        }
    var mListener: NewPlantCallback? = null

    init {
        resetPlant()
        resetTasks()
    }

    fun setOnNewPlantListener(listener: NewPlantCallback) {
        mListener = listener
    }

    fun addTask(newTask: Task) {
        Log.d("Dashboard", "addTask: $newTask")
        tasks.add(hashMapOf(
            "id" to newTask.id,
            "plantId" to newTask.plantId,
            "userId" to newTask.userId,
            "action" to newTask.action,
            "startDate" to newTask.startDate,
            "repeat" to newTask.repeat,
            "occurrence" to newTask.occurrence,
            "lastCompleted" to Timestamp(newTask.lastCompleted)
        ))
        (plant["tasks"] as ArrayList<String>).add(newTask.id)
    }

    fun setImageUrl(url: String) {
        plant["imageUrl"] = url
    }

    fun setStaticParams(id: String, name: String, nick: String, filePath: String, death: Boolean) {
        plant["id"] = id
        plant["name"] = name
        plant["nickname"] = nick
        plant["filePath"] = filePath
        plant["death"] = death
    }

    fun resetPlant() {
        plant["id"] = ""
        plant["imageUrl"] = ""
        plant["filePath"] = ""
        plant["name"] = ""
        plant["nickname"] = ""
        plant["dateAdded"] = DateTimeService.getCurrentDate()
        plant["death"] = false
        plant["tasks"] = ArrayList<String>()
        plant["journal"] = ArrayList<Journal>()
    }

    fun resetTasks() {
        tasks = ArrayList()
    }

    fun notifyPlantRV() {
        mListener?.onPlantAdded()
    }
}