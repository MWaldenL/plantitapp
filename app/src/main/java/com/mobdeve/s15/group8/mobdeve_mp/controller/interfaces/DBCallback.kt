package com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces

interface DBCallback {
    fun onDataRetrieved(doc: MutableMap<String, Any>, id: String, type: String)
    fun onComplete()
}