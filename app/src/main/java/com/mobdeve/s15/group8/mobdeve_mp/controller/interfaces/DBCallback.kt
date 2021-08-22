package com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces

/**
 * Used to inform listeners when data is being retrieved and when all data fetching is done
 */
interface DBCallback {
    fun onDataRetrieved(doc: MutableMap<String, Any>, id: String, type: String)
    fun onDataRetrieved(docs: ArrayList<MutableMap<String, Any>>, type: String)
    fun onComplete(tag: String)
    // TODO: add a function to handle empty results
}