package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.type.DateTime
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.DatePickerDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity :
    AppCompatActivity(),
    DatePickerDialogFragment.OnDateSetListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var btnStartDate: Button
    private lateinit var spinnerOccurrence: Spinner
    private lateinit var spinnerAction: Spinner
    private lateinit var etRepeat: EditText
    private lateinit var mAction: String
    private lateinit var mOccurrence: String

    private lateinit var mStartDate: Calendar

    private lateinit var llRepeatsOn: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        llRepeatsOn = findViewById(R.id.ll_repeats_on)
        btnStartDate = findViewById(R.id.btn_start_date)

        llRepeatsOn.visibility = View.INVISIBLE

        mStartDate = DateTimeService.getCurrentDateWithoutTime()
        mDisplayDateInBtn()
        btnStartDate.setOnClickListener {
            val newFragment = DatePickerDialogFragment(mStartDate)
            newFragment.show(supportFragmentManager, "datePicker")
        }

        mInitSpinnerAction()
        mInitSpinnerOccurrence()
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
        val format1 = SimpleDateFormat("EEE, MMM d, yyyy")
        btnStartDate.text = format1.format(mStartDate.time)
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