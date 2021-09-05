package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.R
import java.lang.ClassCastException

class PlantDeathDialogFragment:
    DialogFragment()
{
    private lateinit var btnPlantDeath: Button
    private lateinit var btnPlantDeathCancel: Button

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
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_plant_death, null)

            btnPlantDeath = view.findViewById(R.id.btn_plant_death)
            btnPlantDeathCancel = view.findViewById(R.id.btn_plant_death_cancel)

            btnPlantDeath.setOnClickListener {
                listener.onPlantDeath(this)
                this.dismiss()
            }

            btnPlantDeathCancel.setOnClickListener {
                this.dismiss()
            }

            builder.setView(view).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}