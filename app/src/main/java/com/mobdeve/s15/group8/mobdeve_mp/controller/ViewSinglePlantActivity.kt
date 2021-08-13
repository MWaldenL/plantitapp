package com.mobdeve.s15.group8.mobdeve_mp.controller

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.JournalDataHelper
import com.mobdeve.s15.group8.mobdeve_mp.model.TaskDataHelper

class ViewSinglePlantActivity : AppCompatActivity() {
    private lateinit var recyclerViewTask: RecyclerView
    private lateinit var recyclerViewJournal: RecyclerView

    private lateinit var ibtnPlantOptions: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_single_plant)

        ibtnPlantOptions = findViewById(R.id.ibtn_plant_options)

        ibtnPlantOptions.setOnClickListener {
            showPopup(ibtnPlantOptions)
        }

        val taskData = TaskDataHelper().fetchData()

        recyclerViewTask = findViewById(R.id.recyclerview_tasks)
        recyclerViewTask.adapter = TaskListAdapter(taskData)
        recyclerViewTask.layoutManager = LinearLayoutManager(this)

        val journalData = JournalDataHelper().fetchData()

        recyclerViewJournal = findViewById(R.id.recyclerview_recent_journal)
        recyclerViewJournal.adapter = JournalListAdapter(journalData)
        recyclerViewJournal.layoutManager = LinearLayoutManager(this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showPopup(view: View) {
        val popup = PopupMenu(this, view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.plant_menu, popup.menu)

        popup.setOnMenuItemClickListener {

            when (it.itemId) {
//                R.id.header1 -> {
//                    Toast.makeText(this@ViewSinglePlantActivity, item.title, Toast.LENGTH_SHORT).show()
//                }
//                R.id.header2 -> {
//                    Toast.makeText(this@ViewSinglePlantActivity, item.title, Toast.LENGTH_SHORT).show()
//                }
//                R.id.header3 -> {
//                    Toast.makeText(this@ViewSinglePlantActivity, item.title, Toast.LENGTH_SHORT).show()
//                }
            }

            true
        }

        popup.setForceShowIcon(true)
        popup.show()
    }
}