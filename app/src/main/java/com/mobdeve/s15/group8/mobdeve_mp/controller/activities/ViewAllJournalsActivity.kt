package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

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
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ViewAllJournalsActivity :
    AppCompatActivity(),
    AddJournalDialogFragment.AddJournalDialogListener
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNickname: TextView
    private lateinit var tvCommonName: TextView
    private lateinit var fabAddNewJournal: FloatingActionButton
    private lateinit var mID: String
    private lateinit var mJournal: ArrayList<Journal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_journals)
        mInitViews()
        mBindData()
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
        val nickname = intent.getStringExtra(getString(R.string.NICKNAME_KEY))
        val name = intent.getStringExtra(getString(R.string.COMMON_NAME_KEY))

        mID = intent.getStringExtra(getString(R.string.ID_KEY)).toString()
        mJournal = intent.getSerializableExtra(getString(R.string.ALL_JOURNALS_KEY)) as ArrayList<Journal>

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
    override fun onSave(dialog: DialogFragment, text: String) {
        val body = text
        val date = LocalDateTime.now().toString()

        val toAdd: HashMap<*, *> = hashMapOf(
            "body" to body,
            "date" to date
        )

        DBService().updateDocument(
            F.plantsCollection,
            mID,
            "journal",
            FieldValue.arrayUnion(toAdd)
        )

        mJournal.add(0, Journal(body, date))
        recyclerView.adapter?.notifyItemInserted(0)
    }

    override fun onCancel(dialog: DialogFragment) {
        // TODO: Notify user???
    }
}