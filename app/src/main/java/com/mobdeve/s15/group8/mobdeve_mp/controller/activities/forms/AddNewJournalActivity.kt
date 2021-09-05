package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.forms

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.BaseActivity

class AddNewJournalActivity : BaseActivity() {
    private lateinit var etJournal: EditText
    private lateinit var tvName: TextView
    private lateinit var tvCharCount: TextView
    private lateinit var btnSaveJournal: Button
    override val layoutResourceId: Int = R.layout.activity_add_new_journal
    override val mainViewId: Int = R.id.layout_add_journal

    override fun inititalizeViews() {
        etJournal = findViewById(R.id.et_journal)
        tvName = findViewById(R.id.tv_name_journal)
        tvCharCount = findViewById(R.id.tv_char_count)
        btnSaveJournal = findViewById(R.id.btn_save_journal)
    }

    override fun bindData() {
        tvName.text = intent.getStringExtra(getString(R.string.NICKNAME_KEY))
        btnSaveJournal.isEnabled = false
    }

    override fun bindActions() {
        etJournal.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = 500 - s!!.length
                tvCharCount.text = length.toString()
                btnSaveJournal.isEnabled = !s.isEmpty()
            }
        })
        btnSaveJournal.setOnClickListener {
            val resultIntent = Intent()

            resultIntent.putExtra(getString(R.string.JOURNAL_KEY), etJournal.text.toString())
            setResult(Activity.RESULT_OK, resultIntent)

            finish()
        }
    }
}