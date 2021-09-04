package com.mobdeve.s15.group8.mobdeve_mp.controller.services

import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.utils.ObjectUtils
import com.mobdeve.s15.group8.mobdeve_mp.BuildConfig
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.ImageUploadCallback
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * The main image uploader service for the app. This service allows modules to upload an image and
 * listen for results. Call uploadToCloud to upload the image to Cloudinary
 */
object CloudinaryService : UploadCallback, CoroutineScope {
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

    fun deleteFromCloud(url: String) {
        val urlSplit = url.split('/')
        val folder = urlSplit[urlSplit.lastIndex-1]
        val id = urlSplit[urlSplit.lastIndex].split('.')[0]
        val imageId = "$folder/$id"
        Log.d("CService", "Deleting $imageId")
        launch(Dispatchers.IO) {
            val result = MediaManager.get().cloudinary
                .uploader()
                .destroy(imageId, ObjectUtils.emptyMap())
            Log.d("CService", "deleted image $result")
        }
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

    override val coroutineContext: CoroutineContext = Dispatchers.IO
}