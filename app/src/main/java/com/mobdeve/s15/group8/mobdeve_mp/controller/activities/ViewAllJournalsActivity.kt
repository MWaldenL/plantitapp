package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.DeleteJournalDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.JournalAllListAdapter
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ViewAllJournalsActivity :
    AppCompatActivity(),
    DeleteJournalDialogFragment.DeleteJournalDialogListener
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNickname: TextView
    private lateinit var tvCommonName: TextView
    private lateinit var tvJournalHeader: TextView
    private lateinit var tvNoJournal: TextView
    private lateinit var fabAddNewJournal: FloatingActionButton
    private lateinit var mJournal: ArrayList<Journal>
    private lateinit var mPlantData: Plant
    private lateinit var mRecentlyDeletedItem: Journal
    private var mRecentlyDeletedPosition: Int = -1

    private var mAddNewJournalLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val string = result.data?.getStringExtra(getString(R.string.JOURNAL_KEY))
                if (string != null) {
                    mOnJournalSave(string)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_journals)
        mPlantData = intent.getParcelableExtra(getString(R.string.PLANT_KEY))!!

        mInitViews()
        mBindData()
        mPrepareSwipeCallback()
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra(getString(R.string.PLANT_KEY), mPlantData)

        setResult(Activity.RESULT_OK, resultIntent)

        super.onBackPressed()
    }

    private fun mInitViews() {
        tvNickname = findViewById(R.id.tv_nickname_journal)
        tvCommonName = findViewById(R.id.tv_common_name_journal)
        tvJournalHeader = findViewById(R.id.tv_journal_header)
        tvNoJournal = findViewById(R.id.tv_no_journal)
        fabAddNewJournal = findViewById(R.id.fab_add_new_journal)

        recyclerView = findViewById(R.id.recyclerview_all_journal)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fabAddNewJournal.setOnClickListener { mHandleNewJournalRequest() }
    }

    private fun mBindData() {
        val (id, userId, imageUrl, filePath, name, nickname, datePurchased, death, taskIds, journal) = mPlantData

        if (nickname == "") {
            tvCommonName.visibility = View.GONE
            tvNickname.text = name
        } else {
            tvCommonName.text = name
            tvNickname.text = nickname
        }

        mJournal = journal
        mJournal = mJournal
            .indices
            .map{i: Int -> mJournal[mJournal.size - 1 - i]}
            .toCollection(ArrayList())

        recyclerView.adapter = JournalAllListAdapter(mJournal)

        if (death)
            fabAddNewJournal.visibility = View.GONE

        mToggleJournalDisplay()
    }

    // TODO: Fix display
    private fun mPrepareSwipeCallback() {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                mRecentlyDeletedItem = mJournal.get(viewHolder.adapterPosition)
                mRecentlyDeletedPosition = viewHolder.adapterPosition

                mJournal.removeAt(mRecentlyDeletedPosition)
                recyclerView.adapter?.notifyItemRemoved(mRecentlyDeletedPosition)
                mHandleDeleteJournalRequest()
            }

            // TODO: Check why icon not showing
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                if (dX != 0f && isCurrentlyActive) {
                    val itemView = viewHolder.itemView
                    val color = Paint()

                    color.color = Color.parseColor("#CC6363")
                    val icon = ContextCompat.getDrawable(this@ViewAllJournalsActivity, R.drawable.ic_trash_24)!!
                    icon.setTint(ResourcesCompat.getColor(resources, R.color.white, null))

                    val top = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                    val left = itemView.width - icon.intrinsicWidth - (itemView.height - icon.intrinsicHeight) / 2
                    val right = left + icon.intrinsicHeight
                    val bottom = top + icon.intrinsicHeight

                    if (dX < 0) {
                        val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat())
                        c.drawRect(background, color)
                        icon.setBounds(left, top, right, bottom)
                    }

                    icon.draw(c)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun mHandleNewJournalRequest() {
        val intent = Intent(this, AddNewJournalActivity::class.java)
        intent.putExtra(getString(R.string.NICKNAME_KEY), tvNickname.text)
        mAddNewJournalLauncher.launch(intent)
    }

    private fun mHandleDeleteJournalRequest() {
        val fragment = DeleteJournalDialogFragment()
        fragment.show(supportFragmentManager, "delete_journal")
    }

    private fun mToggleJournalDisplay() {
        if (mJournal.size == 0) {
            tvJournalHeader.visibility = View.GONE
            tvNoJournal.visibility = View.VISIBLE
        } else {
            tvNoJournal.visibility = View.GONE
            tvJournalHeader.visibility = View.VISIBLE
        }
    }

    private fun mOnJournalSave(text: String) {
        val body = text
        val date = DateTimeService.getCurrentDateTime()

        val toAdd: HashMap<*, *> = hashMapOf(
            "body" to body,
            "date" to date
        )

        // add to db
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

        // notify adapter of addition
        mJournal.add(0, Journal(body, date))
        mToggleJournalDisplay()

        recyclerView.layoutManager?.smoothScrollToPosition(recyclerView, null, 0)
        recyclerView.adapter?.notifyItemInserted(0)
    }

    override fun onJournalDelete(dialog: DialogFragment) {
        val toRemove: HashMap<*, *> = hashMapOf(
            "body" to mRecentlyDeletedItem.body,
            "date" to mRecentlyDeletedItem.date
        )

        // add to db
        DBService.updateDocument(
            F.plantsCollection,
            mPlantData.id,
            "journal",
            FieldValue.arrayRemove(toRemove)
        )

        val index = PlantRepository.plantList.indexOf(mPlantData)

        // remove from local repo
        PlantRepository
            .plantList[index]
            .journal
            .remove(mRecentlyDeletedItem)

        // update plant data
        mPlantData = PlantRepository.plantList[index]
        mToggleJournalDisplay()

        Log.d("hatdog", mPlantData.journal.toString())

        // no need to notify adapter since notification was done before dialog launch
    }

    override fun onJournalDeleteCancel(dialog: DialogFragment) {
        mJournal.add(mRecentlyDeletedPosition, mRecentlyDeletedItem)
        recyclerView.adapter?.notifyItemInserted(mRecentlyDeletedPosition)
    }
}