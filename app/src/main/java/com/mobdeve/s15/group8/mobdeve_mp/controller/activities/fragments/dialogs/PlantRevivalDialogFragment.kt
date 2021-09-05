package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.R
import java.lang.ClassCastException

class PlantRevivalDialogFragment:
    DialogFragment()
{
    private lateinit var btnPlantRevival: Button
    private lateinit var btnPlantRevivalCancel: Button

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
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_plant_revival, null)

            btnPlantRevival = view.findViewById(R.id.btn_plant_revival)
            btnPlantRevivalCancel = view.findViewById(R.id.btn_plant_revival_cancel)

            btnPlantRevival.setOnClickListener {
                listener.onPlantRevival(this)
                this.dismiss()
            }

            btnPlantRevivalCancel.setOnClickListener {
                this.dismiss()
            }

            builder.setView(view).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}