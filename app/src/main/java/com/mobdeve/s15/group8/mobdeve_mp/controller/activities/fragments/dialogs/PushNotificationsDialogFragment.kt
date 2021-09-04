package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.R
import java.lang.ClassCastException

class PushNotificationsDialogFragment:
    DialogFragment()
{
    private lateinit var btnPushAccept: Button
    private lateinit var btnPushDecline: Button

    internal lateinit var listener: PushNotificationsDialogListener

    interface PushNotificationsDialogListener {
        fun onPushAccept(dialog: DialogFragment)
        fun onPushDecline(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as PushNotificationsDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement PushNotificationsDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_push_notifications, null)

            btnPushAccept = view.findViewById(R.id.btn_push_accept)
            btnPushDecline = view.findViewById(R.id.btn_push_decline)

            btnPushAccept.setOnClickListener {
                listener.onPushAccept(this)
                this.dismiss()
            }

            btnPushDecline.setOnClickListener {
                listener.onPushDecline(this)
                this.dismiss()
            }

            builder.setView(view).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}