package com.mobdeve.s15.group8.mobdeve_mp.model.services

import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository

object PlantTaskService {
    fun findPlantById(id: String): Plant? {
        for (plant in PlantRepository.plantList) {
            if (plant.id == id)
                return plant
        }
        return null
    }

    fun findPlantByName(name: String): Plant? {
        for (plant in PlantRepository.plantList) {
            if (plant.name == name)
                return plant
        }
        return null
    }
}