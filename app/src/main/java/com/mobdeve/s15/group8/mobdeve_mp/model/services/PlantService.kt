package com.mobdeve.s15.group8.mobdeve_mp.model.services

import android.util.Log
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.CloudinaryService
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import java.util.*

object PlantService {
    fun findPlantById(id: String): Plant? {
        for (plant in PlantRepository.plantList) {
            if (plant.id == id)
                return plant
        }
        Log.d("Dashboard", "plant not found")
        return null
    }

    fun findPlantByName(name: String): Plant? {
        for (plant in PlantRepository.plantList) {
            if (plant.name == name)
                return plant
        }
        return null
    }

    fun findPlantByNickname(nickname: String): Plant? {
        for (plant in PlantRepository.plantList) {
            if (plant.nickname == nickname)
                return plant
        }
        return null
    }

    fun getPlantDeath(id: String): Boolean {
        for (plant in PlantRepository.plantList) {
            if (plant.id == id)
                return plant.death
        }

        return false
    }

    fun deleteAllPlants(justDead: Boolean) {
        val toDelete = if (justDead) PlantRepository.plantList.filter { p -> p.death } else PlantRepository.plantList

        for (plant in toDelete) {
            delete(plant)
        }

        if (!justDead) // to clean
            PlantRepository.resetData()
    }

    private fun delete(plant: Plant) {
        CloudinaryService.deleteFromCloud(plant.imageUrl)

        // delete from db
        DBService.deleteDocument(
            F.plantsCollection,
            plant.id
        )

        // delete from local repo
        PlantRepository.plantList.remove(plant)

        // delete plant's tasks
        for (taskId in plant.tasks) {
            // remove from local
            PlantRepository.taskList.remove(TaskService.findTaskById(taskId))
            // remove from db
            DBService.deleteDocument(
                F.tasksCollection,
                taskId
            )
        }
    }
}