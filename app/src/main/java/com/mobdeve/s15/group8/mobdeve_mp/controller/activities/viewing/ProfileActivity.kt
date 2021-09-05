package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.viewing

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.FieldValue
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.BaseActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.LoginActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.AppFeedbackDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.NotificationService
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.UserService
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import com.mobdeve.s15.group8.mobdeve_mp.singletons.FeedbackPermissions
import com.mobdeve.s15.group8.mobdeve_mp.singletons.GoogleSingleton
import com.mobdeve.s15.group8.mobdeve_mp.singletons.PushPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class ProfileActivity :
    BaseActivity(),
    AppFeedbackDialogFragment.AppFeedbackDialogListener,
    CoroutineScope
{
    private lateinit var tvProfileName: TextView
    private lateinit var tvDateJoined: TextView
    private lateinit var tvLiveCount: TextView
    private lateinit var tvDeadCount: TextView
    private lateinit var tvTotalCount: TextView
    private lateinit var switchPush: Switch
    private lateinit var switchFeedback: Switch
    private lateinit var btnFeedback: Button
    private lateinit var btnLogout: Button

    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mEditor: SharedPreferences.Editor

    override val coroutineContext: CoroutineContext = Dispatchers.IO
    override val layoutResourceId: Int = R.layout.activity_profile
    override val mainViewId: Int = R.id.layout_profile

    override fun inititalizeViews() {
        tvProfileName = findViewById(R.id.tv_profile_name)
        tvDateJoined = findViewById(R.id.tv_profile_date)
        tvLiveCount = findViewById(R.id.tv_live_count)
        tvDeadCount = findViewById(R.id.tv_dead_count)
        tvTotalCount = findViewById(R.id.tv_total_count)
        switchPush = findViewById(R.id.switch_push)
        switchFeedback = findViewById(R.id.switch_feedback)
        btnFeedback = findViewById(R.id.btn_feedback)
        btnLogout = findViewById(R.id.btn_logout)
    }

    override fun bindData() {
        mSharedPreferences = this.getSharedPreferences(getString(R.string.SP_NAME), Context.MODE_PRIVATE)
        mEditor = mSharedPreferences.edit()

        tvProfileName.text = F.auth.currentUser?.displayName
        launch(Dispatchers.IO){
            val dateJoined = UserService.getUserField("dateJoined").toString()
            withContext(Dispatchers.Main) {
                tvDateJoined.text = dateJoined
            }
        }

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

        switchPush.isChecked = mSharedPreferences.getInt(getString(R.string.SP_PUSH_KEY), -1) == PushPermissions.ALLOWED.ordinal
        switchFeedback.isChecked = mSharedPreferences.getInt(getString(R.string.SP_FEED_KEY), -1) == FeedbackPermissions.ALLOWED.ordinal
    }

    override fun bindActions() {
        switchPush.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                NotificationService.scheduleNotification(this)
                mEditor.putInt(getString(R.string.SP_PUSH_KEY), PushPermissions.ALLOWED.ordinal)
            } else {
                mEditor.putInt(getString(R.string.SP_PUSH_KEY), PushPermissions.NOT_ALLOWED.ordinal)
            }

            mEditor.apply()
        }

        switchFeedback.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mEditor.putInt(getString(R.string.SP_FEED_KEY), FeedbackPermissions.ALLOWED.ordinal)
                mEditor.putInt(getString(R.string.SP_FEED_TIME_KEY), 0) // trigger feedback dialog on next startup
            } else {
                mEditor.putInt(getString(R.string.SP_FEED_KEY), FeedbackPermissions.NOT_ALLOWED.ordinal)
            }

            mEditor.apply()
        }

        btnFeedback.setOnClickListener {
            val fragment = AppFeedbackDialogFragment(true)
            fragment.show(supportFragmentManager, "feedback")
        }

        btnLogout.setOnClickListener {
            PlantRepository.resetData()
            F.auth.signOut()
            GoogleSignIn.getClient(this, GoogleSingleton.googleSigninOptions).signOut()
            Log.d("Dashboard", "Logging out")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onFeedbackContinue(dialog: DialogFragment, feedbackRating: Int, feedbackComment: String) {
        mEditor.putInt(
            getString(R.string.SP_FEED_TIME_KEY),
            TimeUnit.HOURS.convert(Date().time, TimeUnit.MILLISECONDS).toInt()
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

    override fun onFeedbackCancel(dialog: DialogFragment) {
        mEditor.putInt(
            getString(R.string.SP_FEED_TIME_KEY),
            TimeUnit.HOURS.convert(Date().time, TimeUnit.MILLISECONDS).toInt()
        )

        mEditor.apply()
    }

    override fun onFeedbackStop(dialog: DialogFragment) {
        // do nothing, button not displayed
    }
}