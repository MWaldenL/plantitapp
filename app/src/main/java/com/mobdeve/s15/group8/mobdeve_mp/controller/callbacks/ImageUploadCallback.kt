package com.mobdeve.s15.group8.mobdeve_mp.controller.callbacks

/**
 * Informs listeners if images have been uploaded
 */
interface ImageUploadCallback {
    fun onImageUploadSuccess(imageUrl: String)
}