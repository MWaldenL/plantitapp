package com.mobdeve.s15.group8.mobdeve_mp.controller

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import java.io.File
import java.io.FileOutputStream

object CameraService {
    fun reduceImageQuality(filename: String) {
        val bm = BitmapFactory.decodeFile(filename)
        val file = File(filename)
        file.createNewFile()
        val fos = FileOutputStream(file)
        bm.compress(Bitmap.CompressFormat.JPEG, 20, fos)
        fos.close()
    }

    fun getBitmapFromUri(uri: Uri, contentResolver: ContentResolver): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch(e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createImageFile(context: Context): File? {
        val timeStamp = DateTimeService.getCurrentDateTime()
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