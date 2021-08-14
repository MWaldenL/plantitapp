package com.mobdeve.s15.group8.mobdeve_mp.controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.JournalListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.model.JournalDataHelper

class ViewAllJournalsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_journals)

//        val journalData = JournalDataHelper().fetchData()

        recyclerView = findViewById(R.id.recyclerview_all_journal)
        recyclerView.adapter = JournalListAdapter(journalData, false)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}