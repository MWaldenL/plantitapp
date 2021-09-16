package com.mobdeve.s15.group8.mobdeve_mp.controller.services

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import java.io.File
import java.io.FileOutputStream

object CameraService {
    fun launchCameraAndGetFilename(context: Context,
                                   authority: String,
                                   launcher: ActivityResultLauncher<Intent>): String {
        // Declare intent
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.resolveActivity(context.packageManager)

        // Create image file
        val photoFile: File = mCreateImageFile(context) ?: return ""
        val photoURI: Uri = FileProvider.getUriForFile(context, authority, photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        launcher.launch(takePictureIntent)
        return photoFile.absolutePath
    }

    private fun mCreateImageFile(context: Context): File? {
        val timeStamp = DateTimeService.getCurrentDateCal().timeInMillis
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val filename = "plant_${timeStamp}"

        return try {
            File.createTempFile(filename, ".jpg", storageDir)
        } catch(e: Exception) {
            e.printStackTrace()
            null
        }
    }
}