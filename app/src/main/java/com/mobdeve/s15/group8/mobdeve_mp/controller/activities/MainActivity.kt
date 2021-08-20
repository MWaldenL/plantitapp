package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import com.mobdeve.s15.group8.mobdeve_mp.singletons.GoogleSingleton
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.AppFeedbackDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import java.util.*

class MainActivity:
    AppCompatActivity(),
    AppFeedbackDialogFragment.AppFeedbackDialogListener
{
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabAddPlant: FloatingActionButton
    private lateinit var btnTrigger: Button

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

        btnTrigger = findViewById(R.id.btn_trigger)
        btnTrigger.setOnClickListener {
            val fragment = AppFeedbackDialogFragment()
            fragment.show(supportFragmentManager, "feedback")
        }
    }

    override fun onFeedbackContinue(
        dialog: DialogFragment,
        feedbackRating: Float,
        feedbackComment: String
    ) {
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
        val id = F.auth.currentUser?.uid

        DBService.updateDocument(
            F.usersCollection,
            id,
            "feedback_stop",
            true
        )
    }

    override fun onFeedbackCancel(dialog: DialogFragment) {
        val id = F.auth.currentUser?.uid

        DBService.updateDocument(
            F.usersCollection,
            id,
            "feedback_stop",
            false
        )

        DBService.updateDocument(
            F.usersCollection,
            id,
            "feedback_last_sent",
            Date()
        )
    }
}
