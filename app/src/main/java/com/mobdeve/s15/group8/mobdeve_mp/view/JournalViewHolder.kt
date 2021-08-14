package com.mobdeve.s15.group8.mobdeve_mp.view

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal

class JournalViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val tvJournalBody: TextView = itemView.findViewById(R.id.tv_journal_body)
    private val tvJournalDate: TextView = itemView.findViewById(R.id.tv_journal_date)

    fun bindData(journal: Journal) {
        tvJournalBody.text = journal.body
        tvJournalDate.text = journal.date
    }
}