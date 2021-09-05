package com.mobdeve.s15.group8.mobdeve_mp.controller.services

import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.utils.ObjectUtils
import com.mobdeve.s15.group8.mobdeve_mp.BuildConfig
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * The main image uploader service for the app. This service allows modules to upload an image and
 * listen for results. Call uploadToCloud to upload the image to Cloudinary
 */
object CloudinaryService : UploadCallback, CoroutineScope {
    private var mListener: ((imageUrl: String) -> Unit?)? = null

    fun setOnUploadSuccessListener(listener: (s: String) -> Unit) {
        mListener = listener
    }

    fun uploadToCloud(filename: String, plantId: String) {
        MediaManager
            .get()
            .upload(filename)
            .option("folder", F.auth.currentUser!!.uid)
            .option("public_id", plantId)
            .unsigned(BuildConfig.UPLOAD_PRESET)
            .callback(this)
            .dispatch()
    }

    fun deleteFromCloud(url: String) {
        if (url.isEmpty()) return
        val imageId = getPublicId(url)
        Log.d("CService", "Deleting $imageId")
        launch(Dispatchers.IO) {
            val result = MediaManager.get().cloudinary
                .uploader()
                .destroy(imageId, ObjectUtils.emptyMap())
            Log.d("CService", "deleted image $result")
        }
    }

    fun getPublicId(url: String, withFolder: Boolean=true): String {
        val urlSplit = url.split('/')
        val folder = urlSplit[urlSplit.lastIndex-1]
        val id = urlSplit[urlSplit.lastIndex].split('.')[0]
        return if (withFolder) "$folder/$id" else id
    }

    override fun onStart(requestId: String?) {
        Log.d("CLOUDINARY", "Uploading with id $requestId")
    }

    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
    }

    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
        Log.d("CLOUDINARY", "Uploaded to cloud, $resultData")
        val imageUrl = resultData?.get("secure_url").toString()
        mListener?.invoke(imageUrl)
    }

    override fun onError(requestId: String?, error: ErrorInfo?) {
        Log.d("CLOUDINARY", "Error: ${error.toString()}, $requestId")
    }

    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
    }

    override val coroutineContext: CoroutineContext = Dispatchers.IO
}