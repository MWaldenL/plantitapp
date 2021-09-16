package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.viewing

import android.app.Activity
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.BaseActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.forms.AddNewJournalActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.forms.EditPlantActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.*
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.DeletePlantDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.PlantDeathDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.PlantRevivalDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.JournalListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.TaskListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.CloudinaryService
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.ImageLoadingService
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.TaskService
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import java.util.*
import kotlin.collections.ArrayList

class ViewSinglePlantActivity : BaseActivity(),
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
    private lateinit var tvNoJournal: TextView
    private lateinit var tvNoTasks: TextView
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
                    bindData()
                }
            }
        }
    override val layoutResourceId: Int = R.layout.activity_view_single_plant
    override val mainViewId: Int = R.id.layout_view_plant

    override fun onCreate(savedInstanceState: Bundle?) {
        mPlantData = intent.getParcelableExtra(getString(R.string.PLANT_KEY))!!
        super.onCreate(savedInstanceState)
    }

    override fun inititalizeViews() {
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
        tvNoJournal = findViewById(R.id.tv_single_plant_no_journal)
        tvNoTasks = findViewById(R.id.tv_single_plant_no_tasks)

        recyclerViewTask = findViewById(R.id.recyclerview_tasks)
        recyclerViewJournal = findViewById(R.id.recyclerview_all_journal)
        recyclerViewTask.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewJournal.layoutManager = LinearLayoutManager(this)
    }

    override fun bindData() {
        val (id, userId, imageUrl, filePath, name, nickname, datePurchased, death, taskIds, journal) = mPlantData
        val tasksTodayAll = TaskService.getTasksToday()
        val tasks = ArrayList<Task>()
        for (t in tasksTodayAll)
            if (t.plantId == id)
                tasks.add(t)

        Log.d("hello", tasks.toString())

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
            ibtnEditPlant.visibility = View.GONE
            ibtnAddNewJournal.visibility = View.GONE
        } else {
            ibtnRevivePlant.visibility = View.GONE
        }

        ImageLoadingService.loadImage(
            filePath=mPlantData.filePath,
            imageUrl=mPlantData.imageUrl,
            context=this,
            imageView=ivPlant)

        val size = journal.size
        mJournalLimited.clear()

        for (i in 1..3) {
            if (size == i - 1)
                break
            mJournalLimited.add(journal[size - i])
        }

        if (tasks.size == 0)
            tvNoTasks.visibility = View.VISIBLE
        else
            tvNoTasks.visibility = View.GONE

        recyclerViewTask.adapter = TaskListAdapter(tasks)
        recyclerViewJournal.adapter = JournalListAdapter(mJournalLimited)
    }

    override fun bindActions() {
        ibtnAddNewJournal.setOnClickListener { mHandleNewJournalRequest() }
        ibtnEditPlant.setOnClickListener { mHandleEditPlant() }
        ibtnKillPlant.setOnClickListener { mHandlePlantDeath() }
        ibtnRevivePlant.setOnClickListener { mHandlePlantRevival() }
        ibtnDeletePlant.setOnClickListener { mHandlePlantDelete() }
        btnViewAll.setOnClickListener { mGotoViewAllJournalsActivity() }
    }

    override fun onResume() {
        super.onResume()
        mShowOrHideNoJournal()
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
        CloudinaryService.deleteFromCloud(mPlantData.imageUrl)

        // delete from db
        DBService.deleteDocument(
            F.plantsCollection,
            mPlantData.id
        )

        // delete from local repo
        PlantRepository.plantList.remove(mPlantData)

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
            "$name has been deleted. Returning to the home screen.",
            Toast.LENGTH_SHORT
        ).show()

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

        mShowOrHideNoJournal()
    }

    private fun mShowOrHideNoJournal() {
        if (mJournalLimited.size == 0) {
            if (mPlantData.death)
                tvNoJournal.text = "You do not have any journal entries for this plant."

            tvNoJournal.visibility = View.VISIBLE
        } else {
            tvNoJournal.visibility = View.INVISIBLE
        }
    }
}
