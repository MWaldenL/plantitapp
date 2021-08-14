package com.mobdeve.s15.group8.mobdeve_mp.model.repositories

import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object NewPlantInstance {
    var plant: HashMap<String, Any> = HashMap()

    init {
        plant["imageUrl"] = ""
        plant["name"] = ""
        plant["nickname"] = ""
        plant["dateAdded"] = SimpleDateFormat("MM/dd/yyyy").format(Date())
        plant["tasks"] = ArrayList<Task>()
        plant["journal"] = ArrayList<Journal>()
    }

    fun addTask(task: Task) {
        (plant["tasks"] as ArrayList<Task>).add(task)
    }

    fun setPlantName(name: String) {
        plant["name"] = name
    }

    fun setPlantNickname(nickname: String) {
        plant["nickname"] = nickname
    }

    fun setPlantInstance() {

    }
}