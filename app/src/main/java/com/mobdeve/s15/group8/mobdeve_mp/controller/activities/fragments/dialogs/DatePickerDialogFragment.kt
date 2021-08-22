package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerDialogFragment(private val cal: Calendar) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    lateinit var datePickerCallbackListener: OnDateSetListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        datePickerCallbackListener = context as OnDateSetListener
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        datePickerCallbackListener.onDateSet(view, year, month, day)
    }

    interface OnDateSetListener {
        fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int)
    }
}