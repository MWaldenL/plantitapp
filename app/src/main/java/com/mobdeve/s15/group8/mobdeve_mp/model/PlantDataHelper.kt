package com.mobdeve.s15.group8.mobdeve_mp.model

class PlantDataHelper {
    fun fetchData(): ArrayList<Plant> {
        // temp implementation: in the future, fetch plant data from firebase
        val data = ArrayList<Plant>()
        data.add(Plant("hatdog", "HatODg", "sample.jpg"))
        data.add(Plant("dghat", "Mona Megistus", "mona.jpg"))
        data.add(Plant("fklsdjfkldjs", "keqing", "keqing.jpg"))
        data.add(Plant("fklsdjfkldjs", "llala", "adfdf.jpg"))

        return data
    }
}