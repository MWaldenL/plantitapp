package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.*
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.DeletePlantDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.PlantDeathDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.PlantRevivalDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.JournalListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.TaskListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.TaskService
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class ViewSinglePlantActivity :
    AppCompatActivity(),
    DeletePlantDialogFragment.DeletePlantDialogListener,
    PlantDeathDialogFragment.PlantDeathDialogListener,
    PlantRevivalDialogFragment.PlantRevivalDialogListener
{
    private lateinit var recyclerViewTask: RecyclerView
    private lateinit var recyclerViewJournal: RecyclerView
    private lateinit var ibtnAddNewJournal: ImageButton
    private lateinit var ibtnEditPlant: ImageButton
    private lateinit var ibtnKillPlant: ImageButton
    private lateinit var ibtnRevivePlant: ImageButton
    private lateinit var ibtnDeletePlant: ImageButton
    private lateinit var tvCommonName: TextView
    private lateinit var tvNickname: TextView
    private lateinit var tvPurchaseDate: TextView
    private lateinit var ivPlant: ImageView
    private lateinit var ivPlantNameIcon: ImageView
    private lateinit var btnViewAll: Button
    private lateinit var mPlantData: Plant
    private var mJournalLimited = arrayListOf<Journal>()

    private var mViewAllJournalsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getParcelableExtra<Plant>(getString(R.string.PLANT_KEY))
                if (data != null) {
                    mPlantData = data
                    val journal = mPlantData.journal

                    val size = journal.size
                    mJournalLimited.clear()

                    for (i in 1..3) {
                        if (size == i - 1)
                            break
                        mJournalLimited.add(journal[size - i])
                    }

                    recyclerViewJournal.adapter = JournalListAdapter(mJournalLimited)
                }
            }
        }

    private var mAddNewJournalLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val string = result.data?.getStringExtra(getString(R.string.JOURNAL_KEY))
                if (string != null) {
                    mOnJournalSave(string)
                }
            }
        }

    private var mEditPlantLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getParcelableExtra<Plant>(getString(R.string.PLANT_KEY))
                if (data != null) {
                    mPlantData = data
                    mBindData()
                    Log.d("hatdog", mPlantData.toString())
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_single_plant)
        mPlantData = intent.getParcelableExtra(getString(R.string.PLANT_KEY))!!

        mInitViews()
        mBindData()
    }

    // activity init functions

    private fun mInitViews() {
        tvCommonName = findViewById(R.id.tv_common_name)
        tvNickname = findViewById(R.id.tv_nickname)
        tvPurchaseDate = findViewById(R.id.tv_purchase_date)
        ivPlant = findViewById(R.id.iv_plant)
        ivPlantNameIcon = findViewById(R.id.iv_plant_name_icon)
        btnViewAll = findViewById(R.id.btn_view_all)
        ibtnAddNewJournal = findViewById(R.id.ibtn_add_journal)
        ibtnEditPlant = findViewById(R.id.ibtn_edit_plant)
        ibtnKillPlant = findViewById(R.id.ibtn_kill_plant)
        ibtnRevivePlant = findViewById(R.id.ibtn_revive_plant)
        ibtnDeletePlant = findViewById(R.id.ibtn_delete_plant)

        ibtnAddNewJournal.setOnClickListener { mHandleNewJournalRequest() }
        ibtnEditPlant.setOnClickListener { mHandleEditPlant() }
        ibtnKillPlant.setOnClickListener { mHandlePlantDeath() }
        ibtnRevivePlant.setOnClickListener { mHandlePlantRevival() }
        ibtnDeletePlant.setOnClickListener { mHandlePlantDelete() }
        btnViewAll.setOnClickListener { mGotoViewAllJournalsActivity() }

        recyclerViewTask = findViewById(R.id.recyclerview_tasks)
        recyclerViewJournal = findViewById(R.id.recyclerview_all_journal)
        recyclerViewTask.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewJournal.layoutManager = LinearLayoutManager(this)
    }

    private fun mBindData() {
        val (id, userId, imageUrl, filePath, name, nickname, datePurchased, death, taskIds, journal) = mPlantData
        val tasks = TaskService.findTasksByPlantId(id)

        if (nickname == "") {
            tvCommonName.visibility = View.GONE
            ivPlantNameIcon.visibility = View.GONE
            tvCommonName.text = ""
            tvNickname.text = name
        } else {
            tvCommonName.text = name
            tvNickname.text = nickname
        }

        if (death) {
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)
            ivPlant.colorFilter = ColorMatrixColorFilter(matrix)
        }

        tvPurchaseDate.text = datePurchased

        if (death) {
            ibtnKillPlant.visibility = View.GONE

            val params = ibtnEditPlant.layoutParams as ConstraintLayout.LayoutParams
            params.bottomToBottom = tvNickname.id
            params.topToTop = tvNickname.id
            params.endToStart = ibtnRevivePlant.id
            ibtnEditPlant.requestLayout()
        } else {
            ibtnRevivePlant.visibility = View.GONE
        }

        if (filePath == "") {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivPlant)
        } else {
            val imgFile = File(filePath)
            val bmp = BitmapFactory.decodeFile(imgFile.absolutePath)
            ivPlant.setImageBitmap(bmp)
        }

        val size = journal.size
        mJournalLimited.clear()

        for (i in 1..3) {
            if (size == i - 1)
                break
            mJournalLimited.add(journal[size - i])
        }

        recyclerViewTask.adapter = TaskListAdapter(tasks)
        recyclerViewJournal.adapter = JournalListAdapter(mJournalLimited)
    }

    // plant option functions

    private fun mHandleEditPlant() {
        val intent = Intent(this, EditPlantActivity::class.java)
        intent.putExtra(getString(R.string.PLANT_KEY), mPlantData)
        mEditPlantLauncher.launch(intent)
    }

    private fun mHandlePlantDelete() {
        val fragment = DeletePlantDialogFragment()
        fragment.show(supportFragmentManager, "delete_plant")
    }

    private fun mHandlePlantDeath() {
        val fragment = PlantDeathDialogFragment()
        fragment.show(supportFragmentManager, "plant_death")
    }

    private fun mHandlePlantRevival() {
        val fragment = PlantRevivalDialogFragment()
        fragment.show(supportFragmentManager, "plant_revival")
    }

    override fun onPlantDelete(dialog: DialogFragment) {
        val name = if (mPlantData.nickname != "") mPlantData.nickname else mPlantData.name

        // delete from db
        DBService.deleteDocument(
            F.plantsCollection,
            mPlantData.id
        )

        // delete from local repo
        PlantRepository
            .plantList
            .remove(mPlantData)

        // delete plant's tasks
        for (taskId in mPlantData.tasks) {
            // remove from local
            PlantRepository.taskList.remove(TaskService.findTaskById(taskId))
            // remove from db
            DBService.deleteDocument(
                F.tasksCollection,
                taskId
            )
        }

        Toast.makeText(
            this,
            "${name} has been deleted. Returning to the home screen.",
            Toast.LENGTH_SHORT
        ).show()

        // launch main activity after deletion TODO
        finish()
    }

    override fun onPlantDeath(dialog: DialogFragment) {
        val name = if (mPlantData.nickname != "") mPlantData.nickname else mPlantData.name
        val index = PlantRepository.plantList.indexOf(mPlantData)

        DBService.updateDocument(
            F.plantsCollection,
            mPlantData.id,
            "death",
            true
        )

        PlantRepository
            .plantList[index]
            .death = true

        mPlantData = PlantRepository.plantList[index]

        Toast.makeText(
            this,
            "${name} has been marked as dead.",
            Toast.LENGTH_SHORT
        ).show()

        dialog.dismiss()

        finish()
    }

    override fun onPlantRevival(dialog: DialogFragment) {
        val name = if (mPlantData.nickname != "") mPlantData.nickname else mPlantData.name
        val index = PlantRepository.plantList.indexOf(mPlantData)

        DBService.updateDocument(
            F.plantsCollection,
            mPlantData.id,
            "death",
            false
        )

        PlantRepository
            .plantList[index]
            .death = false

        mPlantData = PlantRepository.plantList[index]

        Toast.makeText(
            this,
            "${name} has been revived.",
            Toast.LENGTH_SHORT
        ).show()

        dialog.dismiss()

        finish()
    }

    // task functions



    // journal functions

    private fun mGotoViewAllJournalsActivity() {
        val intent = Intent(this@ViewSinglePlantActivity, ViewAllJournalsActivity::class.java)
        intent.putExtra(getString(R.string.PLANT_KEY), mPlantData)
        mViewAllJournalsLauncher.launch(intent)
    }

    private fun mHandleNewJournalRequest() {
        val intent = Intent(this, AddNewJournalActivity::class.java)
        intent.putExtra(getString(R.string.NICKNAME_KEY), tvNickname.text)
        mAddNewJournalLauncher.launch(intent)
    }

    private fun mOnJournalSave(text: String) {
        val body = text
        val date = DateTimeService.getCurrentDateTime()

        val toAdd: HashMap<*, *> = hashMapOf(
            "body" to body,
            "date" to date
        )

        // add to firebase
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

        // update plant data
        mPlantData = PlantRepository.plantList[index]

        // notify adapter of removal
        if (mJournalLimited.size >= 3) {
            mJournalLimited.removeAt(2)
            recyclerViewJournal.adapter?.notifyItemRemoved(2)
        }

        // notify adapter of addition
        mJournalLimited.add(0, Journal(body, date))
        recyclerViewJournal.adapter?.notifyItemInserted(0)
    }

}
