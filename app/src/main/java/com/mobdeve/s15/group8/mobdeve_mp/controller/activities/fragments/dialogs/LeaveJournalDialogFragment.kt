package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.R
import java.lang.ClassCastException

class LeaveJournalDialogFragment:
    DialogFragment()
{
    private lateinit var btnLeaveJournal: Button
    private lateinit var btnLeaveJournalCancel: Button

    internal lateinit var listener: LeaveJournalDialogListener

    interface LeaveJournalDialogListener {
        fun onJournalLeave(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as LeaveJournalDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement LeaveJournalDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_leave_journal, null)

            btnLeaveJournal = view.findViewById(R.id.btn_leave_journal)
            btnLeaveJournalCancel = view.findViewById(R.id.btn_leave_journal_cancel)

            btnLeaveJournal.setOnClickListener {
                listener.onJournalLeave(this)
                this.dismiss()
            }

            btnLeaveJournalCancel.setOnClickListener {
                this.dismiss()
            }

            builder.setView(view).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}