package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.R
import java.lang.ClassCastException

class DeletePlantDialogFragment:
    DialogFragment()
{
    private lateinit var btnDeletePlant: Button
    private lateinit var btnDeletePlantCancel: Button

    internal lateinit var listener: DeletePlantDialogListener

    interface DeletePlantDialogListener {
        fun onPlantDelete(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DeletePlantDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement DeletePlantDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_delete_plant, null)

            btnDeletePlant = view.findViewById(R.id.btn_delete_plant)
            btnDeletePlantCancel = view.findViewById(R.id.btn_delete_plant_cancel)

            btnDeletePlant.setOnClickListener {
                listener.onPlantDelete(this)
                this.dismiss()
            }

            btnDeletePlantCancel.setOnClickListener {
                this.dismiss()
            }

            builder.setView(view).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}