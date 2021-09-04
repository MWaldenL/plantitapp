package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

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

            builder
                .setMessage(
                    "This plant will be labelled as dead but it will not be deleted.\n\n" +
                            "You can still choose to revive it later on but the following functions will be disabled during the period when it's dead: \n\n" +
                            "- Edit Plant\n" +
                            "- Add New Journal (but you can still delete entries)"
                )
                .setPositiveButton("Continue") { dialog, id ->
                    listener.onPlantDeath(this)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    getDialog()?.cancel()
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}