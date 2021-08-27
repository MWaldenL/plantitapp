package com.mobdeve.s15.group8.mobdeve_mp.controller.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders.JournalAllViewHolder
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Journal
import com.mobdeve.s15.group8.mobdeve_mp.controller.viewholders.JournalViewHolder

class JournalAllListAdapter(private val data: ArrayList<Journal>):
    RecyclerView.Adapter<JournalAllViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalAllViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemLayout = R.layout.item_journal_all
        val view = inflater.inflate(itemLayout, parent, false)
        return JournalAllViewHolder(view)
    }

    override fun onBindViewHolder(holder: JournalAllViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}