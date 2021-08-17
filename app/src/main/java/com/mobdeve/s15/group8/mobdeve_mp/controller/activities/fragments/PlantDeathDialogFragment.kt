package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

class PlantDeathDialogFragment:
    DialogFragment()
{
    internal lateinit var listener: PlantDeathDialogListener

    interface PlantDeathDialogListener {
        fun onPlantDeath(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as PlantDeathDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() + " must implement DeletePlantDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            // TODO: change message to be more specific

            builder
                .setMessage("This plant will be labelled as dead but will not be deleted. Its functions will be limited but you can choose to revive it later on.")
                .setPositiveButton("Continue") { dialog, id ->
                    listener.onPlantDeath(this)
                    getDialog()?.cancel()
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    getDialog()?.cancel()
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}