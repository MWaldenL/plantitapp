package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.type.DateTime
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.DatePickerDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddTaskActivity :
    AppCompatActivity(),
    DatePickerDialogFragment.OnDateSetListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var btnStartDate: Button
    private lateinit var spinnerOccurrence: Spinner
    private lateinit var spinnerAction: Spinner
    private lateinit var occurrenceArrayAdapter: ArrayAdapter<String>
    private lateinit var etRepeat: EditText
    private lateinit var ibtnSaveTask: ImageButton

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        llRepeatsOn = findViewById(R.id.ll_repeats_on)
        btnStartDate = findViewById(R.id.btn_start_date)
        ibtnSaveTask = findViewById(R.id.ibtn_save_task)
        etRepeat = findViewById(R.id.et_repeat)
        tbtnSun = findViewById(R.id.tbtn_sun)
        tbtnMon = findViewById(R.id.tbtn_mon)
        tbtnTue = findViewById(R.id.tbtn_tue)
        tbtnWed = findViewById(R.id.tbtn_wed)
        tbtnThu = findViewById(R.id.tbtn_thu)
        tbtnFri = findViewById(R.id.tbtn_fri)
        tbtnSat = findViewById(R.id.tbtn_sat)

        llRepeatsOn.visibility = View.INVISIBLE

        mAction = "Water"
        mOccurrence = "Day"
        mStartDate = DateTimeService.getCurrentDateWithoutTime()

        mDisplayDateInBtn()
        btnStartDate.setOnClickListener {
            val newFragment = DatePickerDialogFragment(mStartDate)
            newFragment.show(supportFragmentManager, "datePicker")
        }

        ibtnSaveTask.setOnClickListener {
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

        mInitSpinnerAction()
        mInitSpinnerOccurrence()
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

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        Log.d("AddTask", "Date: $year $month $day")
        mStartDate.set(year, month, day)
        mDisplayDateInBtn()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent?.id) {
            R.id.spinner_actions ->
                mAction = parent.getItemAtPosition(position).toString()
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