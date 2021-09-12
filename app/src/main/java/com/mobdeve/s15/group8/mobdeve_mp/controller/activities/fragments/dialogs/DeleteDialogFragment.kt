package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.R
import java.lang.ClassCastException

class DeleteDialogFragment(justDead: Boolean) :
    DialogFragment()
{
    private lateinit var btnDeleteDialog: Button
    private lateinit var btnDeleteDialogCancel: Button
    private lateinit var tvDeleteDialog: TextView
    private var mJustDead: Boolean = justDead

    internal lateinit var listener: DeleteDialogListener

    interface DeleteDialogListener {
        fun onDeleteConfirm(dialog: DialogFragment, justDead: Boolean)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DeleteDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement DeleteDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_delete, null)

            btnDeleteDialog = view.findViewById(R.id.btn_delete_dialog)
            btnDeleteDialogCancel = view.findViewById(R.id.btn_delete_dialog_cancel)
            tvDeleteDialog = view.findViewById(R.id.tv_delete_dialog)

            if (mJustDead)
                tvDeleteDialog.text = "You are about to delete all of your dead plants. This action cannot be undone."

            btnDeleteDialog.setOnClickListener {
                listener.onDeleteConfirm(this, mJustDead)
                this.dismiss()
            }

            btnDeleteDialogCancel.setOnClickListener {
                this.dismiss()
            }

            builder.setView(view).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}