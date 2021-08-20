package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import androidx.fragment.app.DialogFragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import com.mobdeve.s15.group8.mobdeve_mp.R
import java.lang.ClassCastException

class AppFeedbackDialogFragment :
    DialogFragment()
{
    private lateinit var rbFeedbackRating: RatingBar
    private lateinit var etFeedbackComment: EditText

    internal lateinit var listener: AppFeedbackDialogListener

    interface AppFeedbackDialogListener {
        fun onFeedbackContinue(dialog: DialogFragment, feedbackRating: Float, feedbackComment: String)
        fun onFeedbackCancel(dialog: DialogFragment)
        fun onFeedbackStop(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as AppFeedbackDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() + " must implement AppFeedbackDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_app_feedback, null)

            rbFeedbackRating = view.findViewById(R.id.rb_feedback_rating)
            etFeedbackComment = view.findViewById(R.id.et_feedback_comment)

            builder
                .setView(view)
                .setPositiveButton("Continue") { dialog, id ->
                    val feedbackRating = rbFeedbackRating.rating
                    val feedbackComment = etFeedbackComment.text.toString()

                    listener.onFeedbackContinue(this, feedbackRating, feedbackComment)
                }
                .setNegativeButton("Don't show this again") { dialog, id ->
                    listener.onFeedbackStop(this)
                    getDialog()?.cancel()
                }
                .setNeutralButton("Cancel") { dialog, id ->
                    listener.onFeedbackCancel(this)
                    getDialog()?.cancel()
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
