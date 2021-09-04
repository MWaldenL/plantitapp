package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.DailyNotificationsDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.UserService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.UserService.coroutineContext
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var tvProfileName: TextView
    private lateinit var tvLiveCount: TextView
    private lateinit var tvDeadCount: TextView
    private lateinit var tvTotalCount: TextView
    private lateinit var switchPush: Switch
    private lateinit var switchFeedback: Switch
    private lateinit var btnFeedback: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mInitViews()
        mBindData()
        mBindActions()
    }

    private fun mInitViews() {
        tvProfileName = findViewById(R.id.tv_profile_name)
        tvLiveCount = findViewById(R.id.tv_live_count)
        tvDeadCount = findViewById(R.id.tv_dead_count)
        tvTotalCount = findViewById(R.id.tv_total_count)

        switchPush = findViewById(R.id.switch_push)
        switchFeedback = findViewById(R.id.switch_feedback)

        btnFeedback = findViewById(R.id.btn_feedback)
        btnLogout = findViewById(R.id.btn_logout)
    }

    private fun mBindData() {
        tvProfileName.text = F.auth.currentUser?.displayName

        var dead = 0
        var live = 0

        for (plant in PlantRepository.plantList) {
            if (plant.death)
                dead++
            else
                live++
        }

        tvLiveCount.text = live.toString()
        tvDeadCount.text = dead.toString()
        tvTotalCount.text = (live + dead).toString()

        // TODO: bind permissions
    }

    private fun mBindActions() {
        // TODO: bind actions
    }
}