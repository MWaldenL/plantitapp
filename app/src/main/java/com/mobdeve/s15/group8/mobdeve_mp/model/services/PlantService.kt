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
        if (justDead) {
            val toDelete = PlantRepository.plantList.filter { p -> p.death }

            for (plant in toDelete) {
                deleteFromCloudDB(plant)
                PlantRepository.deletePlant(plant)
            }
        } else {
            for (plant in PlantRepository.plantList) {
                deleteFromCloudDB(plant)
            }

            PlantRepository.resetData()
        }
    }

    private fun deleteFromCloudDB(plant: Plant) {
        for (taskId in plant.tasks) {
            DBService.deleteDocument(
                F.tasksCollection,
                taskId
            )
        }

        CloudinaryService.deleteFromCloud(plant.imageUrl)
        DBService.deleteDocument(
            F.plantsCollection,
            plant.id
        )
    }
}