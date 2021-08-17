package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.mobdeve.s15.group8.mobdeve_mp.F
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.JournalListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.TaskListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.AddJournalDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import java.io.File
import java.time.LocalDateTime

class ViewSinglePlantActivity :
    AppCompatActivity(),
    AddJournalDialogFragment.AddJournalDialogListener
{
    private lateinit var recyclerViewTask: RecyclerView
    private lateinit var recyclerViewJournal: RecyclerView
    private lateinit var ibtnPlantOptions: ImageButton
    private lateinit var ibtnAddNewJournal: ImageButton
    private lateinit var tvCommonName: TextView
    private lateinit var tvNickname: TextView
    private lateinit var tvPurchaseDate: TextView
    private lateinit var ivPlant: ImageView
    private lateinit var btnViewAll: Button
    private lateinit var mPlantData: Plant

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

    private var mJournalLimited = arrayListOf<Journal>()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_single_plant)

        mInitViews()
        mBindData()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun mInitViews() {
        tvCommonName = findViewById(R.id.tv_common_name)
        tvNickname = findViewById(R.id.tv_nickname)
        tvPurchaseDate = findViewById(R.id.tv_purchase_date)
        ivPlant = findViewById(R.id.iv_plant)
        btnViewAll = findViewById(R.id.btn_view_all)
        ibtnPlantOptions = findViewById(R.id.ibtn_plant_options)
        ibtnAddNewJournal = findViewById(R.id.ibtn_add_journal)

        ibtnAddNewJournal.setOnClickListener { mHandleNewJournalRequest() }
        ibtnPlantOptions.setOnClickListener { mShowPopup(ibtnPlantOptions) }
        btnViewAll.setOnClickListener { mGotoViewAllJournalsActivity() }

        recyclerViewTask = findViewById(R.id.recyclerview_tasks)
        recyclerViewJournal = findViewById(R.id.recyclerview_all_journal)
        recyclerViewTask.layoutManager = LinearLayoutManager(this)
        recyclerViewJournal.layoutManager = LinearLayoutManager(this)
    }

    private fun mBindData() {
        mPlantData = intent.getParcelableExtra(getString(R.string.PLANT_KEY))!!

        val (id, imageUrl, filePath, name, nickname, datePurchased, tasks, journal) = mPlantData

        if (nickname == "") {
            tvCommonName.visibility = View.GONE
            tvCommonName.text = ""
            tvNickname.text = name
        } else {
            tvCommonName.text = name
            tvNickname.text = nickname
        }

        tvPurchaseDate.text = datePurchased

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
        for (i in 1..3) {
            if (size == i - 1)
                break
            mJournalLimited.add(journal[size - i])
        }
        recyclerViewTask.adapter = TaskListAdapter(tasks)
        recyclerViewJournal.adapter = JournalListAdapter(mJournalLimited)
    }

    private fun mHandleNewJournalRequest() {
        val fragment = AddJournalDialogFragment()
        val bundle = Bundle()
        bundle.putString(getString(R.string.NICKNAME_KEY), tvNickname.text.toString())
        fragment.arguments = bundle
        fragment.show(supportFragmentManager, "add_journal")
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onJournalSave(dialog: DialogFragment, text: String) {
        val body = text
        val date = LocalDateTime.now().toString()
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

        // update plant data, unnecessary but for consistency of data
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

    override fun onJournalCancel(dialog: DialogFragment) {
        // TODO: Notify user???
    }

    private fun mGotoViewAllJournalsActivity() {
        val intent = Intent(this@ViewSinglePlantActivity, ViewAllJournalsActivity::class.java)
        intent.putExtra(getString(R.string.PLANT_KEY), mPlantData)
        mViewAllJournalsLauncher.launch(intent)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun mShowPopup(view: View) {
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
