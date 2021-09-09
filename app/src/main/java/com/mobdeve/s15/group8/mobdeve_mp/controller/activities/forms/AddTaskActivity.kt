package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.forms

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.*
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.BaseActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.DatePickerDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.NewPlantInstance
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.TaskService
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddTaskActivity : BaseActivity(),
    DatePickerDialogFragment.OnDateSetListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var btnStartDate: Button
    private lateinit var spinnerOccurrence: Spinner
    private lateinit var spinnerAction: Spinner
    private lateinit var occurrenceArrayAdapter: ArrayAdapter<String>
    private lateinit var etRepeat: EditText
    private lateinit var ibtnSaveTask: ImageButton
    private lateinit var tvErrWeeklyRecurrence: TextView
    private lateinit var tvErrActionExists: TextView

    private lateinit var tbtnSun: ToggleButton
    private lateinit var tbtnMon: ToggleButton
    private lateinit var tbtnTue: ToggleButton
    private lateinit var tbtnWed: ToggleButton
    private lateinit var tbtnThu: ToggleButton
    private lateinit var tbtnFri: ToggleButton
    private lateinit var tbtnSat: ToggleButton

    private lateinit var llRepeatsOn: LinearLayout

    private lateinit var mAction: String
    private lateinit var mOccurrence: String
    private lateinit var mStartDate: Calendar
    private var mNewTasks: ArrayList<Task>? = null
    private var mOldTasks: ArrayList<String>? = null

    override val layoutResourceId: Int = R.layout.activity_add_task
    override val mainViewId: Int = R.id.layout_add_task

    override fun inititalizeViews() {
        llRepeatsOn = findViewById(R.id.ll_repeats_on)
        btnStartDate = findViewById(R.id.btn_start_date)
        ibtnSaveTask = findViewById(R.id.ibtn_save_task)
        tvErrWeeklyRecurrence = findViewById(R.id.tv_err_weekly_recurrence)
        tvErrActionExists = findViewById(R.id.tv_err_action_exists)
        etRepeat = findViewById(R.id.et_repeat)
        tbtnSun = findViewById(R.id.tbtn_sun)
        tbtnMon = findViewById(R.id.tbtn_mon)
        tbtnTue = findViewById(R.id.tbtn_tue)
        tbtnWed = findViewById(R.id.tbtn_wed)
        tbtnThu = findViewById(R.id.tbtn_thu)
        tbtnFri = findViewById(R.id.tbtn_fri)
        tbtnSat = findViewById(R.id.tbtn_sat)

        llRepeatsOn.visibility = View.INVISIBLE
        mInitSpinnerAction()
        mInitSpinnerOccurrence()
    }

    override fun bindData() {
        mAction = "Water"
        mOccurrence = "Day"
        mStartDate = DateTimeService.getCurrentDateWithoutTime()
        mNewTasks = intent.getSerializableExtra(getString(R.string.EDIT_PLANT_TASKS)) as ArrayList<Task>?
        mOldTasks = intent.getSerializableExtra(getString(R.string.EDIT_PLANT_OLD_TASKS)) as ArrayList<String>?
        mDisplayDateInBtn()
    }

    override fun bindActions() {
        btnStartDate.setOnClickListener {
            val newFragment = DatePickerDialogFragment(mStartDate)
            newFragment.show(supportFragmentManager, "datePicker")
        }
        ibtnSaveTask.setOnClickListener {
            if (mCheckFields()) {
                val resultIntent = Intent()
                resultIntent.putExtra(
                    getString(R.string.ADD_TASK_ACTION), mAction)
                resultIntent.putExtra(
                    getString(R.string.ADD_TASK_START_DATE), mStartDate.timeInMillis)
                resultIntent.putExtra(getString(
                    R.string.ADD_TASK_OCCURRENCE), mOccurrence)
                resultIntent.putExtra(getString(
                    R.string.ADD_TASK_REPEAT), etRepeat.text.toString().toInt())
                resultIntent.putIntegerArrayListExtra(getString(
                    R.string.ADD_TASK_WEEKLY_RECURRENCE), mGetWeeklyRecurrence())

                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun mGetWeeklyRecurrence(): ArrayList<Int> {
        val weeklyRecurrence = ArrayList<Int>()
        if (mOccurrence == "Week")
            if (tbtnSun.isChecked)
                weeklyRecurrence.add(1)
            if (tbtnMon.isChecked)
                weeklyRecurrence.add(2)
            if (tbtnTue.isChecked)
                weeklyRecurrence.add(3)
            if (tbtnWed.isChecked)
                weeklyRecurrence.add(4)
            if (tbtnThu.isChecked)
                weeklyRecurrence.add(5)
            if (tbtnFri.isChecked)
                weeklyRecurrence.add(6)
            if (tbtnSat.isChecked)
                weeklyRecurrence.add(7)
        return weeklyRecurrence
    }

    private fun mInitSpinnerAction() {
        spinnerAction = findViewById(R.id.spinner_actions)
        ArrayAdapter.createFromResource(
            this,
            R.array.actions_array,
            R.layout.item_spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.item_spinner)
            spinnerAction.adapter = adapter
        }
        spinnerAction.onItemSelectedListener = this
    }

    private fun mInitSpinnerOccurrence() {
        spinnerOccurrence = findViewById(R.id.spinner_occurrence)
        ArrayAdapter.createFromResource(
            this,
            R.array.occurrence_array,
            R.layout.item_spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.item_spinner)
            spinnerOccurrence.adapter = adapter
        }
        spinnerOccurrence.onItemSelectedListener = this
    }

    @SuppressLint("SimpleDateFormat")
    private fun mDisplayDateInBtn() {
        val format = SimpleDateFormat("EEE, MMM d, yyyy")
        btnStartDate.text = format.format(mStartDate.time)
    }

    private fun mNewPlantTasksNotUnique(): Boolean {
        Log.d("MPAddTaskActivity", "newPlantInstance: $mNewTasks")
        return NewPlantInstance.tasks.any { t -> t["action"] == mAction }
    }

    private fun mEditPlantTasksNotUnique(): Boolean {
        var editPlantTasksNotUnique = false
        if (!mNewTasks.isNullOrEmpty()) {
            Log.d("MPAddTaskActivity", "new: $mNewTasks")
            editPlantTasksNotUnique = mNewTasks!!.any { t -> t.action == mAction }
        }
        if (!mOldTasks.isNullOrEmpty()) {
            Log.d("MPAddTaskActivity", "old: $mOldTasks")
            editPlantTasksNotUnique = editPlantTasksNotUnique || mOldTasks!!.any { task ->
                val t = TaskService.findTaskById(task)
                t?.action == mAction
            }
        }
        return editPlantTasksNotUnique
    }

    private fun mDoesTaskHaveExistingAction(): Boolean {
        return mNewPlantTasksNotUnique() || mEditPlantTasksNotUnique()
    }

    private fun mCheckFields(): Boolean {
        var atLeastOneDaySelected = false
        if (mOccurrence == "Week") {
            if (tbtnSun.isChecked or
                tbtnMon.isChecked or
                tbtnTue.isChecked or
                tbtnWed.isChecked or
                tbtnThu.isChecked or
                tbtnFri.isChecked or
                tbtnSat.isChecked)
                atLeastOneDaySelected = true
        } else
            atLeastOneDaySelected = true

        tvErrWeeklyRecurrence.visibility = if (atLeastOneDaySelected) View.GONE else View.VISIBLE
        tvErrActionExists.visibility = if (mDoesTaskHaveExistingAction()) View.VISIBLE else View.GONE

        return atLeastOneDaySelected && !mDoesTaskHaveExistingAction()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        Log.d("AddTask", "Date: $year $month $day")
        mStartDate.set(year, month, day)
        mDisplayDateInBtn()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent?.id) {
            R.id.spinner_actions -> {
                mAction = parent.getItemAtPosition(position).toString()
                tvErrActionExists.visibility = if (mDoesTaskHaveExistingAction()) View.VISIBLE else View.GONE
            }
            R.id.spinner_occurrence -> {
                mOccurrence = parent.getItemAtPosition(position).toString()
                if ((mOccurrence == "Week") or (mOccurrence == "Weeks"))
                    llRepeatsOn.visibility = View.VISIBLE
                else
                    llRepeatsOn.visibility = View.INVISIBLE
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}