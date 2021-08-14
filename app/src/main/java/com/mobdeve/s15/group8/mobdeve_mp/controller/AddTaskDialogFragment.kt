package com.mobdeve.s15.group8.mobdeve_mp.controller

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.R

class AddTaskDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.dialog_add_task, null)

            val spinnerAction: Spinner = view.findViewById(R.id.spinner_actions)
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.actions_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerAction.adapter = adapter
            }

            val spinnerOccurrence: Spinner = view.findViewById(R.id.spinner_occurrence)
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.occurrence_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerOccurrence.adapter = adapter
            }

            builder.setView(view)
                .setPositiveButton("Add",
                    DialogInterface.OnClickListener { dialog, id ->
                        // TODO: connect sa logic
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })

            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}