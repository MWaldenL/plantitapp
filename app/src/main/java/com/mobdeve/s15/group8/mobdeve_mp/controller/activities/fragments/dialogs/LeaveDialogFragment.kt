package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.singletons.LeaveDialogType
import java.lang.ClassCastException

class LeaveDialogFragment(type: Int) :
    DialogFragment()
{
    private lateinit var btnLeave: Button
    private lateinit var btnLeaveCancel: Button
    private lateinit var tvLeavingQuestion: TextView
    private val mType = type

    internal lateinit var listener: LeaveDialogListener

    interface LeaveDialogListener {
        fun onJournalLeave(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as LeaveDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement LeaveDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_leaving, null)

            btnLeave = view.findViewById(R.id.btn_leave)
            btnLeaveCancel = view.findViewById(R.id.btn_leave_cancel)
            tvLeavingQuestion = view.findViewById(R.id.tv_leaving_question)

            when(mType) {
                LeaveDialogType.ADD_JOURNAL.ordinal ->
                    tvLeavingQuestion.text = getString(R.string.leave_journal_question)

                LeaveDialogType.ADD_PLANT.ordinal ->
                    tvLeavingQuestion.text = getString(R.string.leave_add_plant_question)

                LeaveDialogType.EDIT_PLANT.ordinal ->
                    tvLeavingQuestion.text = getString(R.string.leave_edit_plant_question)
            }

            btnLeave.setOnClickListener {
                listener.onJournalLeave(this)
                this.dismiss()
            }

            btnLeaveCancel.setOnClickListener {
                this.dismiss()
            }

            builder.setView(view).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}