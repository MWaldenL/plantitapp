package com.mobdeve.s15.group8.mobdeve_mp.model.repositories

import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.NewPlantCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import kotlinx.parcelize.RawValue
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object NewPlantInstance {
    var plant: HashMap<String, Any> = HashMap()
    lateinit var mListener: NewPlantCallback
    val plantObject: Plant
        get() {
            return Plant(
                plant["id"].toString(),
                plant["imageUrl"].toString(),
                plant["filePath"].toString(),
                plant["name"].toString(),
                plant["nickname"].toString(),
                plant["dateAdded"].toString(),
                plant["death"] as Boolean,
                plant["tasks"] as ArrayList<Task>,
                plant["journal"] as ArrayList<Journal>)
        }

    init {
        resetPlant()
    }

    fun setOnNewPlantListener(listener: NewPlantCallback) {
        mListener = listener
    }

    fun addTask(task: Task) {
        (plant["tasks"] as ArrayList<Task>).add(task)
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
        plant["dateAdded"] = SimpleDateFormat("MM/dd/yyyy").format(Date())
        plant["death"] = false
        plant["tasks"] = ArrayList<Task>()
        plant["journal"] = ArrayList<Journal>()
    }

    fun notifyPlantRV() {
        mListener.updateView()
    }
}