package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.JournalListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal

class ViewAllJournalsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNickname: TextView
    private lateinit var tvCommonName: TextView
    private lateinit var fabAddNewJournal: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_journals)

        val nickname = intent.getStringExtra(getString(R.string.NICKNAME_KEY))
        val name = intent.getStringExtra(getString(R.string.COMMON_NAME_KEY))
        val id = intent.getStringExtra(getString(R.string.ID_KEY))

        tvNickname = findViewById(R.id.tv_nickname_journal)
        tvCommonName = findViewById(R.id.tv_common_name_journal)
        fabAddNewJournal = findViewById(R.id.fab_add_new_journal)

        if (nickname == "") {
            tvCommonName.visibility = View.GONE
            tvNickname.text = name
        } else {
            tvCommonName.text = name
            tvNickname.text = nickname
        }

        val journalData = intent.getSerializableExtra(getString(R.string.ALL_JOURNALS_KEY)) as ArrayList<Journal>

        recyclerView = findViewById(R.id.recyclerview_all_journal)
        recyclerView.adapter = JournalListAdapter(journalData, false)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fabAddNewJournal.setOnClickListener {
            val fragment = AddJournalDialogFragment()

            val bundle = Bundle()
            bundle.putString(getString(R.string.NICKNAME_KEY), tvNickname.text.toString())
            bundle.putString(getString(R.string.ID_KEY), id)

            fragment.arguments = bundle
            fragment.show(supportFragmentManager, "add_journal")
        }
    }
}