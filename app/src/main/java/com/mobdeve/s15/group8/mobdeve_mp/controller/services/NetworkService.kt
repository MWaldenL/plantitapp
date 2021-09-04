package com.mobdeve.s15.group8.mobdeve_mp.controller.services

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.lifecycle.LiveData

class NetworkService(context: Context): LiveData<Boolean>() {
    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onActive() {
        super.onActive()
        if (Build.VERSION.SDK_INT >= 29) {
            connectivityManager.registerDefaultNetworkCallback(mGetConnCallback())
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun makeNetworkRequest() {
//        TODO: Implement for lower versions
    }

    private fun mGetConnCallback(): ConnectivityManager.NetworkCallback {
        return object: ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network : Network) {
                super.onAvailable(network)
                postValue(true)
            }

            override fun onLost(network : Network) {
                super.onLost(network)
                postValue(false)
            }
        }
    }
}