package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

class DeletePlantDialogFragment:
    DialogFragment()
{
    internal lateinit var listener: DeletePlantDialogListener

    interface DeletePlantDialogListener {
        fun onPlantDelete(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DeletePlantDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() + " must implement DeletePlantDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder
                .setMessage("You are about to delete this plant. This action cannot be undone.")
                .setPositiveButton("Delete") { dialog, id ->
                    listener.onPlantDelete(this)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    getDialog()?.cancel()
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}