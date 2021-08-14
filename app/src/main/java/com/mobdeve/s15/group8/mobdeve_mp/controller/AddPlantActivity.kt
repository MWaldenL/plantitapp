package com.mobdeve.s15.group8.mobdeve_mp.controller

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.Task

class AddPlantActivity : AppCompatActivity() {

    private var tasks = loadSampleTasks()
    private lateinit var tasksRV: RecyclerView
    private lateinit var addTaskBtn: Button
    private lateinit var deleteTaskIBtn: Button

    // TODO: delete after implementing db
    private fun loadSampleTasks(): ArrayList<Task> {
        val data = ArrayList<Task>()
        data.add(
            Task(
                "Water",
                "August 14, 2021",
                1,
                "daily"
            )
        )
        data.add(
            Task(
                "Water",
                "August 14, 2021",
                1,
                "daily"
            )
        )
        data.add(
            Task(
                "Water",
                "August 14, 2021",
                1,
                "daily"
            )
        )
        data.add(
            Task(
                "Water",
                "August 14, 2021",
                1,
                "daily"
            )
        )
        data.add(
            Task(
                "Water",
                "August 14, 2021",
                1,
                "daily"
            )
        )
        data.add(
            Task(
                "Water",
                "August 14, 2021",
                1,
                "daily"
            )
        )
        return data
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_plant)

        tasksRV = findViewById(R.id.rv_tasks)
        tasksRV.adapter = AddPlantTasksAdapter(tasks)
        tasksRV.layoutManager = LinearLayoutManager(this)

        addTaskBtn = findViewById(R.id.btn_add_task)
        addTaskBtn.setOnClickListener {
            val fragment = AddTaskDialogFragment()
            fragment.show(supportFragmentManager, "add_task")
        }
    }

}