package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cloudinary.android.MediaManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import com.mobdeve.s15.group8.mobdeve_mp.singletons.GoogleSingleton
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.AppFeedbackDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.DailyNotificationsDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.RefreshCallback
import com.mobdeve.s15.group8.mobdeve_mp.controller.receivers.AlarmReceiver
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import com.mobdeve.s15.group8.mobdeve_mp.singletons.FeedbackPermissions
import com.mobdeve.s15.group8.mobdeve_mp.singletons.PushPermissions
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity:
    AppCompatActivity(),
    AppFeedbackDialogFragment.AppFeedbackDialogListener,
    DailyNotificationsDialogFragment.DailyNotificationsDialogListener,
    RefreshCallback
{
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabAddPlant: FloatingActionButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var btnSetAlarm: Button
    private lateinit var btnCancel: Button

    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mEditor: SharedPreferences.Editor

    // TODO: Remove these in final impl, only here for manual cancel
    private lateinit var alarmManager: AlarmManager
    private lateinit var notificationIntent: Intent
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav_view)
        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navFragment.navController
        bottomNav.setupWithNavController(navController)

        fabAddPlant = findViewById(R.id.fab_add_plant)
        fabAddPlant.setOnClickListener {
            val addPlantIntent = Intent(this@MainActivity, AddPlantActivity::class.java)
            startActivity(addPlantIntent)
        }

        mSharedPreferences = this.getSharedPreferences(getString(R.string.SP_NAME), Context.MODE_PRIVATE)
        mEditor = mSharedPreferences.edit()

        mHandleDailyNotificationsReady()

        // TODO: Remove these in final impl, only here for manual cancel
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notificationIntent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 1, notificationIntent, 0)

        btnSetAlarm = findViewById(R.id.btn_set_alarm)
        btnSetAlarm.setOnClickListener {
            mSetAlarm()
        }

        btnCancel = findViewById(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            alarmManager.cancel(pendingIntent)
        }

        PlantRepository.setRefreshedListener(this)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener {
            PlantRepository.getData()
        }
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
        val c = Calendar.getInstance()

        // uncomment for final impl
//        c.set(Calendar.HOUR_OF_DAY, 10)
//        c.set(Calendar.MINUTE, 0)
//        c.set(Calendar.SECOND, 0)
//
//        if (c.before(Calendar.getInstance()))
//            c.add(Calendar.DATE, 1)
//
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent) // change third arg to millis (min 60000) to test repeated

        // code for testing
        c.add(Calendar.SECOND, 10)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent) // use this to check for exact time
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
        Log.d("Main", "${PlantRepository.plantList} ${PlantRepository.taskList}")
        swipeRefreshLayout.isRefreshing = false
        Log.d("Main", "Done refreshing")
    }
}
