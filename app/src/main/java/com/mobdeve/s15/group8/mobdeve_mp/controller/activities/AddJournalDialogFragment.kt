package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.app.AlertDialog
import android.app.Dialog
import androidx.fragment.app.DialogFragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
            var id = "DefaultID"

            etJournal = view.findViewById(R.id.et_journal)
            tvName = view.findViewById(R.id.tv_name_journal)
            tvCharCount = view.findViewById(R.id.tv_char_count)

            val bundle = this.arguments

            if (bundle != null) {
                tvName.text = bundle.getString(getString(R.string.NICKNAME_KEY), "Hello")
                id = bundle.getString(getString(R.string.ID_KEY), "DefaultID")
            }

            etJournal.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val count = 500 - s!!.length
                    tvCharCount.text = count.toString()
                }
            })

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
