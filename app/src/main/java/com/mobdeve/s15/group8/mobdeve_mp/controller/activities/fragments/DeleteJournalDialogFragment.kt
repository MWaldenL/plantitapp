package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

class DeleteJournalDialogFragment:
    DialogFragment()
{
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

            builder
                .setMessage("You are about to delete this journal entry. This action cannot be undone.")
                .setPositiveButton("Delete") { dialog, id ->
                    listener.onJournalDelete(this)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    listener.onJournalDeleteCancel(this)
                    getDialog()?.cancel()
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}