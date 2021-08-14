package com.mobdeve.s15.group8.mobdeve_mp.model

class TaskDataHelper {
    fun fetchData(): ArrayList<Task> {
        // TODO: Fetch task data from firebase
        val data = ArrayList<Task>()
        data.add(Task(false, "water", 14))
        data.add(Task(false, "weed", 12))
        data.add(Task(false, "move to sunlight", 9))
        data.add(Task(false, "fertilize", 20))

        return data
    }
}