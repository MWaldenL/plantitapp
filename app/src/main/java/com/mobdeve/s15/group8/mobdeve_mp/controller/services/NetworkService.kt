package com.mobdeve.s15.group8.mobdeve_mp.controller.services

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData

class NetworkService(context: Context): LiveData<Boolean>() {
    private lateinit var mNetworkCallback: ConnectivityManager.NetworkCallback
    private var mConnectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun onActive() {
        super.onActive()
        updateConnection()
        registerCallback()
    }

    override fun onInactive() {
        super.onInactive()
        unregisterCallback()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun makeNetworkRequest() {
        val requestBuilder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        mConnectivityManager.registerNetworkCallback(requestBuilder.build(), mGetConnCallback())
    }

    private fun mGetConnCallback(): ConnectivityManager.NetworkCallback {
        mNetworkCallback = object: ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network : Network) {
                super.onAvailable(network)
                postValue(true)
            }

            override fun onLost(network : Network) {
                super.onLost(network)
                postValue(false)
            }
        }
        return mNetworkCallback
    }

    fun registerCallback() {
        if (Build.VERSION.SDK_INT >= 25) {
            mConnectivityManager.registerDefaultNetworkCallback(mGetConnCallback())
        } else {
            makeNetworkRequest()
        }
    }

    fun unregisterCallback() {
        try{
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private val networkReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateConnection()
        }
    }

    fun updateConnection() {
        val activeNetwork = mConnectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnected == true)
    }
}