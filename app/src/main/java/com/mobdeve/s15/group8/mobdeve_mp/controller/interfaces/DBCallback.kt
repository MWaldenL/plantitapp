package com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces

/**
 * Used to inform listeners when data is being retrieved and when all data fetching is done
 */
interface DBCallback {
    fun onComplete(tag: String)
    // TODO: add a function to handle empty results
}