package com.mobdeve.s15.group8.mobdeve_mp.controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.JournalDataHelper
import com.mobdeve.s15.group8.mobdeve_mp.model.TaskDataHelper

class ViewSinglePlantActivity : AppCompatActivity() {
    private lateinit var recyclerViewTask: RecyclerView
    private lateinit var recyclerViewJournal: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_single_plant)

        val taskData = TaskDataHelper().fetchData()

        recyclerViewTask = findViewById(R.id.recyclerview_tasks)
        recyclerViewTask.adapter = TaskListAdapter(taskData)
        recyclerViewTask.layoutManager = LinearLayoutManager(this)

        val journalData = JournalDataHelper().fetchData()

        recyclerViewJournal = findViewById(R.id.recyclerview_recent_journal)
        recyclerViewJournal.adapter = JournalListAdapter(journalData)
        recyclerViewJournal.layoutManager = LinearLayoutManager(this)
    }
}