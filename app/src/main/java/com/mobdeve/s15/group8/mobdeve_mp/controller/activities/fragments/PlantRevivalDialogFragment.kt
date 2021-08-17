package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

class PlantRevivalDialogFragment:
    DialogFragment()
{
    internal lateinit var listener: PlantRevivalDialogListener

    interface PlantRevivalDialogListener {
        fun onPlantRevival(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as PlantRevivalDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() + " must implement DeletePlantDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            // TODO: change message to be more specific

            builder
                .setMessage("This plant will no longer be labelled as dead. Its functions will be restored.")
                .setPositiveButton("Continue") { dialog, id ->
                    listener.onPlantRevival(this)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    getDialog()?.cancel()
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}