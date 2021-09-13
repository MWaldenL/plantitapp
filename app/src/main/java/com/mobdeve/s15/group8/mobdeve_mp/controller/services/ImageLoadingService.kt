package com.mobdeve.s15.group8.mobdeve_mp.controller.services

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant

object ImageLoadingService {
    fun loadImage(filePath: String, imageUrl: String="", context: Context, imageView: ImageView) {
        Glide.with(context).load("file:${filePath}")
            .addListener(object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("MPImageLoading", "$e")
                    val handler = Handler(Looper.getMainLooper())
                    handler.post{
                        mLoadImageRemote(imageUrl, context, imageView)
                    }
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("MPImageLoading", "resourceReady")
                    val handler = Handler(Looper.getMainLooper())
                    handler.post { mLoadImageLocal(filePath, context, imageView) }
                    return true
                }
            }).into(imageView)
    }

    private fun mLoadImageRemote(imageUrl: String, context: Context, imageView: ImageView) {
        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.bg_img_temp)
            .into(imageView)
    }

    private fun mLoadImageLocal(filePath: String, context: Context, imageView: ImageView) {
        Glide.with(context)
            .load("file:${filePath}")
            .apply(RequestOptions().override(480, 480).centerCrop())
            .placeholder(R.drawable.bg_img_temp)
            .into(imageView)
    }
}