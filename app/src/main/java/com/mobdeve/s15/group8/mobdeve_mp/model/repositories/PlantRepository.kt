package com.mobdeve.s15.group8.mobdeve_mp.model.repositories

import com.mobdeve.s15.group8.mobdeve_mp.F
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object PlantRepository: CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()
    val plantList: ArrayList<Plant> = ArrayList()

    fun getData() {
        launch(coroutineContext) {
            val res = DBService.readCollection(F.plantsCollection)
            if (res != null) {
                for (doc in res) {
                    val d = doc.data
                    val id = doc.id
                    val journal = ArrayList<Journal>()
                    val tasks = ArrayList<Task>()
                    val imageUrl = d["imageUrl"].toString()
                    val name = d["name"].toString()
                    val nickname = d["nickname"].toString()
                    val dateAdded = d["dateAdded"].toString()
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
                    plantList.add(
                        Plant(
                            id,
                            imageUrl,
                            filePath="",
                            name,
                            nickname,
                            dateAdded,
                            tasks,
                            journal))
                }
            }
        }
    }

}
