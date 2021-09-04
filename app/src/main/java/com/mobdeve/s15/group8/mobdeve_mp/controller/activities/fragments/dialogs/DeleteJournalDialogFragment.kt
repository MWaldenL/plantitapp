package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.R
import java.lang.ClassCastException

class DeleteJournalDialogFragment:
    DialogFragment()
{
    private lateinit var btnDeleteJournal: Button
    private lateinit var btnDeleteJournalCancel: Button

    internal lateinit var listener: DeleteJournalDialogListener

    interface DeleteJournalDialogListener {
        fun onJournalDelete(dialog: DialogFragment)
        fun onJournalDeleteCancel(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DeleteJournalDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() + " must implement DeleteJournalDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_delete_journal, null)

            btnDeleteJournal = view.findViewById(R.id.btn_delete_journal)
            btnDeleteJournalCancel = view.findViewById(R.id.btn_delete_journal_cancel)

            btnDeleteJournal.setOnClickListener {
                listener.onJournalDelete(this)
                this.dismiss()
            }

            btnDeleteJournalCancel.setOnClickListener {
                listener.onJournalDeleteCancel(this)
                this.dismiss()
            }

            builder.setView(view).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}