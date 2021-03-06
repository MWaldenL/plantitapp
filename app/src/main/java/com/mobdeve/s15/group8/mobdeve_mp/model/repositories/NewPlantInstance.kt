package com.mobdeve.s15.group8.mobdeve_mp.model.repositories

import android.util.Log
import com.google.firebase.Timestamp
import com.mobdeve.s15.group8.mobdeve_mp.controller.callbacks.NewPlantCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
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
                id = plant["id"].toString(),
                userId = plant["userId"].toString(),
                imageUrl = plant["imageUrl"].toString(),
                filePath = plant["filePath"].toString(),
                name = plant["name"].toString(),
                nickname = plant["nickname"].toString(),
                dateAdded = plant["dateAdded"].toString(),
                death = plant["death"] as Boolean,
                tasks = plant["tasks"] as ArrayList<String>,
                journal = plant["journal"] as ArrayList<Journal>)
        }
    val tasksObject: ArrayList<Task>
        get() {
            val t: ArrayList<Task> = ArrayList()
            for (task in tasks)
                t.add(Task(
                    task["id"].toString(),
                    task["plantId"].toString(),
                    task["userId"].toString(),
                    task["action"].toString(),
                    (task["startDate"] as Timestamp).toDate(),
                    task["repeat"] as Int,
                    task["occurrence"].toString(),
                    (task["lastCompleted"] as Timestamp).toDate(),
                    task["weeklyRecurrence"] as ArrayList<Int>
                ))
            return t
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
        Log.d("AddTask", "addTask: $newTask")
        tasks.add(hashMapOf(
            "id" to newTask.id,
            "plantId" to newTask.plantId,
            "userId" to newTask.userId,
            "action" to newTask.action,
            "startDate" to Timestamp(newTask.startDate),
            "repeat" to newTask.repeat,
            "occurrence" to newTask.occurrence,
            "lastCompleted" to Timestamp(newTask.lastCompleted),
            "weeklyRecurrence" to newTask.weeklyRecurrence!!
        ))
        (plant["tasks"] as ArrayList<String>).add(newTask.id) // TODO: Remove
    }

    fun removeTask(taskToRemove: Task) {
        Log.d("AddTask", "remove task: $taskToRemove")
        Log.d("AddTask", "before removal: ${tasks.size}, ${tasksObject.size}")
        val idx = tasksObject.indexOf(taskToRemove)
        tasks.remove(tasks[idx])
        tasksObject.remove(taskToRemove)
        Log.d("AddTask", "result after removal: ${tasks.size}, ${tasksObject.size}")
    }

    fun setImageUrl(url: String) {
        plant["imageUrl"] = url
    }

    fun setStaticParams(id: String, userId: String, name: String, nick: String, filePath: String, death: Boolean) {
        plant["id"] = id
        plant["userId"] = userId
        plant["name"] = name
        plant["nickname"] = nick
        plant["filePath"] = filePath
        plant["death"] = death
    }

    fun resetPlant() {
        plant["id"] = ""
        plant["userId"] = ""
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