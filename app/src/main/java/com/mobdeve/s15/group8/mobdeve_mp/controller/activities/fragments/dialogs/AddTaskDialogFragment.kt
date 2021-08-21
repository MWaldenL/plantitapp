package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.NewPlantInstance
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import java.text.DateFormatSymbols
import java.util.*

class AddTaskDialogFragment :
    DialogFragment(),
    AdapterView.OnItemSelectedListener,
    DatePicker.OnDateChangedListener
{
    private lateinit var spinnerOccurrence: Spinner
    private lateinit var spinnerAction: Spinner
    private lateinit var dateStart: DatePicker
    private lateinit var etRepeat: EditText
    private lateinit var mAction: String
    private lateinit var mStartDate: String
    private lateinit var mOccurrence: String
    private var mRepeat: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_add_task, null)
            etRepeat = view.findViewById(R.id.et_repeat)
            mInitDatePicker(view)
            mInitSpinnerAction(view)
            mInitSpinnerOccurrence(view)
            builder
                .setView(view)
                .setPositiveButton("Add") { dialog, id ->
                    mRepeat = etRepeat.text.toString().toInt()
                    Log.d("Dashboard", "add task plant id: ${NewPlantInstance.plantObject}")
                    NewPlantInstance.addTask(
                        Task(
                            UUID.randomUUID().toString(),
                            "",
                            F.auth.uid!!,
                            mAction,
                            mStartDate,
                            mRepeat,
                            mOccurrence,
                            Date()
                        )
                    )
                }
                .setNegativeButton("Cancel") { dialog, id -> getDialog()?.cancel() }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        when(parent?.id) {
            R.id.spinner_actions -> mAction = parent.getItemAtPosition(pos).toString()
            R.id.spinner_occurrence -> mOccurrence = parent.getItemAtPosition(pos).toString()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val month = DateFormatSymbols().months[monthOfYear]
        mStartDate = "$month $dayOfMonth, $year"
    }

    private fun mInitDatePicker(view: View) {
        dateStart = view.findViewById(R.id.date_start)
        val cal: Calendar = Calendar.getInstance()
        val monthInt = cal.get(Calendar.MONTH)
        val month = DateFormatSymbols().months[cal.get(Calendar.MONTH)]
        val year = cal.get(Calendar.YEAR)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        dateStart.init(
            year,
            monthInt,
            day,
            this)
        mStartDate = "$month $day, $year"
    }

    private fun mInitSpinnerAction(view: View) {
        spinnerAction = view.findViewById(R.id.spinner_actions)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.actions_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAction.adapter = adapter
        }
        spinnerAction.onItemSelectedListener = this
    }

    private fun mInitSpinnerOccurrence(view: View) {
        spinnerOccurrence = view.findViewById(R.id.spinner_occurrence)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.occurrence_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOccurrence.adapter = adapter
        }
        spinnerOccurrence.onItemSelectedListener = this
    }
}
