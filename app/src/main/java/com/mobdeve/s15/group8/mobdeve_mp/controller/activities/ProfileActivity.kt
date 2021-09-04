package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import com.mobdeve.s15.group8.mobdeve_mp.R

class ProfileActivity : AppCompatActivity() {
    private lateinit var tvProfileName: TextView
    private lateinit var tvLiveCount: TextView
    private lateinit var tvDeadCount: TextView
    private lateinit var tvTotalCount: TextView
    private lateinit var tvAllTimeCount: TextView
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

    private fun mInitViews() {}

    private fun mBindData() {}

    private fun mBindActions() {}
}