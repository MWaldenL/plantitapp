package com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces

/**
 * Informs listeners if a background task has resolved, completing the refresh
 */
interface RefreshCallback {
    fun onRefreshSuccess()
}