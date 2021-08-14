package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.F
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.AddPlantTasksAdapter
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.NewPlantInstance
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService

class AddPlantActivity : AppCompatActivity() {
    private lateinit var tasksRV: RecyclerView
    private lateinit var btnAddTask: Button
    private lateinit var btnSave: Button
    private lateinit var etPlantName: EditText
    private lateinit var etPlantNickname: EditText
    private lateinit var ibtnDelete: Button
    private val mTasks = NewPlantInstance.plant["tasks"] as ArrayList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_plant)

        etPlantName = findViewById(R.id.et_plant_name)
        etPlantNickname = findViewById(R.id.et_plant_nickname)
        tasksRV = findViewById(R.id.rv_tasks)
        tasksRV.adapter = AddPlantTasksAdapter(mTasks)
        tasksRV.layoutManager = LinearLayoutManager(this)

        btnAddTask = findViewById(R.id.btn_add_task)
        btnAddTask.setOnClickListener {
            val fragment = AddTaskDialogFragment()
            fragment.show(supportFragmentManager, "add_task")
        }

        btnSave = findViewById(R.id.btn_save_plant)
        btnSave.setOnClickListener {
            NewPlantInstance.setPlantName(etPlantName.text.toString())
            NewPlantInstance.setPlantNickname(etPlantNickname.text.toString())
            DBService().addDocument(F.plantsCollection, NewPlantInstance.plant)
        }
    }

}