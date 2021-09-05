package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.fragment.app.DialogFragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import com.mobdeve.s15.group8.mobdeve_mp.R
import java.lang.ClassCastException

class AppFeedbackDialogFragment(forceTrigger: Boolean = false) :
    DialogFragment()
{
    private lateinit var rbFeedbackRating: RatingBar
    private lateinit var etFeedbackComment: EditText
    private lateinit var btnFeedbackContinue: Button
    private lateinit var btnFeedbackStop: Button
    private lateinit var btnFeedbackCancel: Button
    private lateinit var btnFeedbackCancelForced: Button
    private lateinit var tvErrFeedback: TextView

    private val mForceTrigger: Boolean = forceTrigger

    internal lateinit var listener: AppFeedbackDialogListener

    interface AppFeedbackDialogListener {
        fun onFeedbackContinue(dialog: DialogFragment, feedbackRating: Int, feedbackComment: String)
        fun onFeedbackCancel(dialog: DialogFragment)
        fun onFeedbackStop(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as AppFeedbackDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement AppFeedbackDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_app_feedback, null)

            rbFeedbackRating = view.findViewById(R.id.rb_feedback_rating)
            etFeedbackComment = view.findViewById(R.id.et_feedback_comment)
            btnFeedbackContinue = view.findViewById(R.id.btn_feedback_continue)
            btnFeedbackStop = view.findViewById(R.id.btn_feedback_stop)
            btnFeedbackCancel = view.findViewById(R.id.btn_feedback_cancel)
            btnFeedbackCancelForced = view.findViewById(R.id.btn_feedback_cancel_forced)
            tvErrFeedback = view.findViewById(R.id.tv_err_feedback)

            if (mForceTrigger) {
                btnFeedbackStop.visibility = View.GONE
                btnFeedbackCancel.visibility = View.GONE
            } else {
                btnFeedbackCancelForced.visibility = View.GONE
            }

            // TODO: hide error on listen?

            btnFeedbackContinue.setOnClickListener {
                val feedbackRating = rbFeedbackRating.rating.toInt()
                val feedbackComment = etFeedbackComment.text.toString()

                if (feedbackRating == 0 && feedbackComment.isEmpty()) { // require comment if rating == 0. else, allow empty comment
                    tvErrFeedback.visibility = View.VISIBLE
                } else {
                    listener.onFeedbackContinue(this, feedbackRating, feedbackComment)
                    this.dismiss()
                }
            }

            btnFeedbackStop.setOnClickListener {
                listener.onFeedbackStop(this)
                this.dismiss()
            }

            btnFeedbackCancel.setOnClickListener {
                listener.onFeedbackCancel(this)
                this.dismiss()
            }

            builder.setView(view).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onCancel(dialog: DialogInterface) {
        listener.onFeedbackCancel(this)
    }
}
