package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.AppFeedbackDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.DailyNotificationsDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.RefreshCallback
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.NetworkService
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.NotificationService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.singletons.FeedbackPermissions
import com.mobdeve.s15.group8.mobdeve_mp.singletons.PushPermissions
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity:
    AppCompatActivity(),
    AppFeedbackDialogFragment.AppFeedbackDialogListener,
    DailyNotificationsDialogFragment.DailyNotificationsDialogListener,
    RefreshCallback
{
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabAddPlant: FloatingActionButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var layoutMain: CoordinatorLayout
    private lateinit var btnSetAlarm: Button
    private lateinit var ivOffline: ImageView
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mInitViews()
        mInitListeners()

        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navFragment.navController
        bottomNav.setupWithNavController(navController)

        mSharedPreferences = this.getSharedPreferences(getString(R.string.SP_NAME), Context.MODE_PRIVATE)
        mEditor = mSharedPreferences.edit()

        mHandleDailyNotificationsReady()
        PlantRepository.setRefreshedListener(this)

        val networkConnection = NetworkService(this)
        networkConnection.observe(this, { connected ->
            if (connected) {
                Log.d("MPMainActivity", "connected")
                ivOffline.visibility = View.GONE
                mToggleMainViews(show=true)
            } else {
                Log.d("MPMainActivity", "disconnected")
                ivOffline.visibility = View.VISIBLE
                mToggleMainViews(show=false)
            }
        })
    }

    private fun mInitViews() {
        ivOffline = findViewById(R.id.iv_offline_dashboard)
        layoutMain = findViewById(R.id.layout_main)
        bottomNav = findViewById(R.id.bottom_nav_view)
        bottomAppBar = findViewById(R.id.bottom_appbar)
        fabAddPlant = findViewById(R.id.fab_add_plant)
        btnSetAlarm = findViewById(R.id.btn_set_alarm)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh)
    }

    private fun mInitListeners() {
        fabAddPlant.setOnClickListener {
            val addPlantIntent = Intent(this@MainActivity, AddPlantActivity::class.java)
            startActivity(addPlantIntent)
        }
        btnSetAlarm.setOnClickListener {
            mSetAlarm()
        }
        swipeRefreshLayout.setOnRefreshListener {
            PlantRepository.getData()
        }
    }

    private fun mToggleMainViews(show: Boolean) {
        bottomAppBar.visibility = if (show) View.VISIBLE else View.GONE
        bottomNav.visibility = if (show) View.VISIBLE else View.GONE
        fabAddPlant.visibility = if (show) View.VISIBLE else View.GONE
        layoutMain.visibility = if (show) View.VISIBLE else View.GONE
    }

    // push notifications function

    private fun mHandleDailyNotificationsReady() {
        val current = mSharedPreferences.getInt(getString(R.string.SP_PUSH_KEY), -1)
        if (current == -1) {
            val fragment = DailyNotificationsDialogFragment()
            fragment.show(supportFragmentManager, "daily_notifications")
        } else {
            mHandleFeedbackReady()
        }
    }

    override fun onPushAccept(dialog: DialogFragment) {
        mEditor.putInt(getString(R.string.SP_PUSH_KEY), PushPermissions.ALLOWED.ordinal)
        mEditor.apply()

        mSetAlarm()
        mHandleFeedbackReady()
    }

    override fun onPushDecline(dialog: DialogFragment) {
        mEditor.putInt(getString(R.string.SP_PUSH_KEY), PushPermissions.NOT_ALLOWED.ordinal)
        mEditor.apply()

        mHandleFeedbackReady()
    }

    private fun mSetAlarm() {
        NotificationService.scheduleNotification(this)
    }

    // feedback functions

    private fun mHandleFeedbackReady() {
        val current = mSharedPreferences.getInt(getString(R.string.SP_FEED_KEY), -1)

        if (current == -1) {
            val then = mSharedPreferences.getInt(getString(R.string.SP_FEED_TIME_KEY), 0)
            val now = TimeUnit.HOURS.convert(Date().time, TimeUnit.MILLISECONDS).toInt()
            val diff = now - then

            if (diff >= 48) {
                val fragment = AppFeedbackDialogFragment()
                fragment.show(supportFragmentManager, "feedback")
            }
        } else if (current == FeedbackPermissions.ALLOWED.ordinal) {
            val then = mSharedPreferences.getInt(getString(R.string.SP_FEED_TIME_KEY), 0)
            val now = TimeUnit.HOURS.convert(Date().time, TimeUnit.MILLISECONDS).toInt()
            val diff = now - then

            if (diff >= 336) {
                val fragment = AppFeedbackDialogFragment()
                fragment.show(supportFragmentManager, "feedback")
            }
        }
    }

    override fun onFeedbackContinue(dialog: DialogFragment, feedbackRating: Float, feedbackComment: String) {
        mEditor.putInt(
            getString(R.string.SP_FEED_TIME_KEY),
            TimeUnit.HOURS.convert(Date().time, TimeUnit.MILLISECONDS).toInt()
        )
        mEditor.putInt(
            getString(R.string.SP_FEED_KEY),
            FeedbackPermissions.ALLOWED.ordinal
        )

        mEditor.apply()

        val id = F.auth.currentUser?.uid
        val toAdd = hashMapOf(
            "rating" to feedbackRating,
            "comment" to feedbackComment
        )

        DBService.updateDocument(
            F.usersCollection,
            id,
            "feedback",
            FieldValue.arrayUnion(toAdd)
        )
    }

    override fun onFeedbackStop(dialog: DialogFragment) {
        mEditor.putInt(
            getString(R.string.SP_FEED_TIME_KEY),
            TimeUnit.HOURS.convert(Date().time, TimeUnit.MILLISECONDS).toInt()
        )
        mEditor.putInt(
            getString(R.string.SP_FEED_KEY),
            FeedbackPermissions.NOT_ALLOWED.ordinal
        )

        mEditor.apply()
    }

    override fun onFeedbackCancel(dialog: DialogFragment) {
        mEditor.putInt(
            getString(R.string.SP_FEED_TIME_KEY),
            TimeUnit.HOURS.convert(Date().time, TimeUnit.MILLISECONDS).toInt()
        )
        mEditor.putInt(
            getString(R.string.SP_FEED_KEY),
            FeedbackPermissions.ALLOWED.ordinal
        )

        mEditor.apply()
    }

    override fun onRefreshSuccess() {
        if (PlantRepository.plantList.isNotEmpty() and PlantRepository.taskList.isNotEmpty()) {
            Log.d("MPMain", "${PlantRepository.plantList} ${PlantRepository.taskList}")
            swipeRefreshLayout.isRefreshing = false
            Log.d("MPMain", "Done refreshing")
        }
    }
}
