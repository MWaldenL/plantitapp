package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.NetworkService

abstract class BaseActivity: AppCompatActivity() {
    private lateinit var networkService: NetworkService
    private lateinit var viewOffline: ConstraintLayout
    private lateinit var mViewMain: View
    abstract val layoutResourceId: Int
    abstract val mainViewId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        viewOffline = findViewById(R.id.view_offline)
        mViewMain = findViewById(mainViewId)
        inititalizeViews()
        bindData()
        bindActions()
        networkService = NetworkService(this)
        networkService.observe(this, { connected -> toggleViews(connected) })
    }

    override fun onPause() {
        super.onPause()
        networkService.unregisterCallback()
    }

    override fun onResume() {
        super.onResume()
        networkService.registerCallback()
    }

    protected abstract fun inititalizeViews()

    protected abstract fun bindData()

    protected abstract fun bindActions()

    protected open fun toggleViews(connected: Boolean) {
        viewOffline.visibility = if (connected) View.GONE else View.VISIBLE
        mViewMain.visibility = if (connected) View.VISIBLE else View.GONE
    }
}