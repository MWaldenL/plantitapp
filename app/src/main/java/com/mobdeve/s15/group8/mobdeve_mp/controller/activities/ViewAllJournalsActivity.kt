package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.JournalListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal

class ViewAllJournalsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_journals)
        val journalData = intent.getSerializableExtra(getString(R.string.ALL_JOURNALS_KEY)) as ArrayList<Journal>
        recyclerView = findViewById(R.id.recyclerview_all_journal)
        recyclerView.adapter = JournalListAdapter(journalData, false)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}