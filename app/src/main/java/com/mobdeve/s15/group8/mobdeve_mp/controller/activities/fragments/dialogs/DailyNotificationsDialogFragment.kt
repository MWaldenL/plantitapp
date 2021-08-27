package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

class DailyNotificationsDialogFragment:
    DialogFragment()
{
    internal lateinit var listener: DailyNotificationsDialogListener

    interface DailyNotificationsDialogListener {
        fun onPushAccept(dialog: DialogFragment)
        fun onPushDecline(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DailyNotificationsDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() + " must implement DailyNotificationsDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            // TODO: change message to be more specific

            builder
                .setMessage("Do you want to receive push notifications? You can always change this in your profile.")
                .setPositiveButton("Yes") { dialog, id ->
                    listener.onPushAccept(this)
                }
                .setNegativeButton("No") { dialog, id ->
                    listener.onPushDecline(this)
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}