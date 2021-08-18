package com.mobdeve.s15.group8.mobdeve_mp.model.services

import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.ImageUploadCallback

/**
 * The main image uploader service for the app. This service allows modules to upload an image and
 * listen for results. Call uploadToCloud to upload the image to Cloudinary
 */
object ImageUploadService : UploadCallback {
    private var mListener: ImageUploadCallback? = null

    fun setOnUploadSuccessListener(listener: ImageUploadCallback) {
        mListener = listener
    }

    fun uploadToCloud(filename: String) {
        MediaManager
            .get()
            .upload(filename)
            .option("folder", F.auth.currentUser!!.uid)
            .unsigned("vywedaso")
            .callback(this).dispatch()
    }

    override fun onStart(requestId: String?) {
        Log.d("CLOUDINARY", "Uploading with id $requestId")
    }

    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
    }

    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
        Log.d("CLOUDINARY", "Uploaded to cloud, $resultData")
        val imageUrl = resultData?.get("secure_url").toString()
        mListener?.onImageUploadSuccess(imageUrl)
    }

    override fun onError(requestId: String?, error: ErrorInfo?) {
        Log.d("CLOUDINARY", "Error: $error, $requestId")
    }

    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
    }
}