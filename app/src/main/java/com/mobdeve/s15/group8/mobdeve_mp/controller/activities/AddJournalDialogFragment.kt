package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.app.AlertDialog
import android.app.Dialog
import androidx.fragment.app.DialogFragment
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.widget.TextView
import com.mobdeve.s15.group8.mobdeve_mp.R

class AddJournalDialogFragment :
    DialogFragment()
{
    private lateinit var etJournal: EditText
    private lateinit var tvName: TextView
    private lateinit var tvCharCount: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_add_journal, null)

            etJournal = view.findViewById(R.id.et_journal)
            tvCharCount = view.findViewById(R.id.tv_char_count)
            
            etJournal.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP) {
                    tvCharCount.text = etJournal.text.length.toString()
                }
                false
            }

            builder
                .setView(view)
                .setPositiveButton("Save") { dialog, id ->
                    // TODO: edit this
                }
                .setNegativeButton("Cancel") { dialog, id -> getDialog()?.cancel() }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
