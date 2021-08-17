package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import com.mobdeve.s15.group8.mobdeve_mp.F
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.JournalListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.AddJournalDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import java.time.LocalDateTime

class ViewAllJournalsActivity :
    AppCompatActivity(),
    AddJournalDialogFragment.AddJournalDialogListener
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNickname: TextView
    private lateinit var tvCommonName: TextView
    private lateinit var fabAddNewJournal: FloatingActionButton
    private lateinit var mJournal: ArrayList<Journal>
    private lateinit var mPlantData: Plant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_journals)
        mPlantData = intent.getParcelableExtra(getString(R.string.PLANT_KEY))!!
        mInitViews()
        mBindData()
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra(getString(R.string.PLANT_KEY), mPlantData)

        setResult(Activity.RESULT_OK, resultIntent)

        super.onBackPressed()
    }

    private fun mInitViews() {
        tvNickname = findViewById(R.id.tv_nickname_journal)
        tvCommonName = findViewById(R.id.tv_common_name_journal)
        fabAddNewJournal = findViewById(R.id.fab_add_new_journal)

        recyclerView = findViewById(R.id.recyclerview_all_journal)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fabAddNewJournal.setOnClickListener { mHandleNewJournalRequest() }
    }

    private fun mBindData() {
        val nickname = mPlantData.nickname
        val name = mPlantData.name
        mJournal = mPlantData.journal

        mJournal = mJournal
            .indices
            .map{i: Int -> mJournal[mJournal.size - 1 - i]}
            .toCollection(ArrayList())

        if (nickname == "") {
            tvCommonName.visibility = View.GONE
            tvNickname.text = name
        } else {
            tvCommonName.text = name
            tvNickname.text = nickname
        }

        recyclerView.adapter = JournalListAdapter(mJournal, false)
    }

    private fun mHandleNewJournalRequest() {
        val fragment = AddJournalDialogFragment()

        val bundle = Bundle()
        bundle.putString(getString(R.string.NICKNAME_KEY), tvNickname.text.toString())

        fragment.arguments = bundle
        fragment.show(supportFragmentManager, "add_journal")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onJournalSave(dialog: DialogFragment, text: String) {
        val body = text
        val date = LocalDateTime.now().toString()

        val toAdd: HashMap<*, *> = hashMapOf(
            "body" to body,
            "date" to date
        )

        // add to db
        DBService.updateDocument(
            F.plantsCollection,
            mPlantData.id,
            "journal",
            FieldValue.arrayUnion(toAdd)
        )

        val index = PlantRepository.plantList.indexOf(mPlantData)

        // add to local repo
        PlantRepository
            .plantList[index]
            .journal
            .add(Journal(body, date))

        // update plant data, unnecessary but for consistency of data
        mPlantData = PlantRepository.plantList[index]

        // notify adapter of addition
        mJournal.add(0, Journal(body, date))
        recyclerView.layoutManager?.smoothScrollToPosition(recyclerView, null, 0)
        recyclerView.adapter?.notifyItemInserted(0)
    }

    override fun onJournalCancel(dialog: DialogFragment) {
        // TODO: Notify user???
    }
}