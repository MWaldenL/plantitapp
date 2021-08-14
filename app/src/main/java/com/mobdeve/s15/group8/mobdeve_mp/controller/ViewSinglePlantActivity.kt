package com.mobdeve.s15.group8.mobdeve_mp.controller

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.JournalListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.TaskListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.model.Plant

class ViewSinglePlantActivity : AppCompatActivity() {
    private lateinit var recyclerViewTask: RecyclerView
    private lateinit var recyclerViewJournal: RecyclerView
    private lateinit var ibtnPlantOptions: ImageButton
    private lateinit var tvCommonName: TextView
    private lateinit var tvNickname: TextView
    private lateinit var ivPlant: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_single_plant)
        val plantData = intent.getParcelableExtra<Plant>("PLANT_KEY")
        mInitViews()
        mBindData(plantData!!)

    }

    private fun mInitViews() {
        tvCommonName = findViewById(R.id.tv_common_name)
        tvNickname = findViewById(R.id.tv_nickname)
        ivPlant = findViewById(R.id.iv_plant)
        ibtnPlantOptions = findViewById(R.id.ibtn_plant_options)
        ibtnPlantOptions.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                showPopup(ibtnPlantOptions)
            }
        }

        recyclerViewTask = findViewById(R.id.recyclerview_tasks)
        recyclerViewTask.layoutManager = LinearLayoutManager(this)
        recyclerViewJournal = findViewById(R.id.recyclerview_recent_journal)
        recyclerViewJournal.layoutManager = LinearLayoutManager(this)
    }

    private fun mBindData(data: Plant) {
        tvCommonName.text = data.name
        tvNickname.text = data.nickname
        Glide.with(this)
            .load(data.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(ivPlant)
        recyclerViewTask.adapter = TaskListAdapter(data.tasks)
        recyclerViewJournal.adapter = JournalListAdapter(data.journal)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showPopup(view: View) {
        val popup = PopupMenu(this, view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.plant_menu, popup.menu)

        // TODO: define actions for each option

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