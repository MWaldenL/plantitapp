package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.DBCallback
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.NetworkService
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F

class SplashActivity : AppCompatActivity(), DBCallback {
    private lateinit var mNetworkConnection: NetworkService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNetworkConnection = NetworkService(this)
        mNetworkConnection.observe(this, { connected ->
            if (connected) { // only try to fetch if connected
                Log.d("MPSplashActivity", "connected")
                PlantRepository.setOnDataFetchedListener(this) // Wait on the plant repository's fetching
                PlantRepository.getData() // The plant repository will inform us when they are done
            }
        })

        if (F.auth.currentUser == null) { // no user - go to login
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        mNetworkConnection.unregisterCallback()
    }

    override fun onResume() {
        super.onResume()
        mNetworkConnection.registerCallback()
    }

    override fun onComplete(tag: String) { // Once the plant repo has informed us, go to MainActivity
        Log.d("MPSplashActivity", "onComplete $tag")
        PlantRepository.setOnDataFetchedListener(null)
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}