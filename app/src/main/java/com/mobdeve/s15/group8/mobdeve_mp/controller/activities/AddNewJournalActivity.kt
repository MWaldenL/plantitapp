package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.mobdeve.s15.group8.mobdeve_mp.R

class AddNewJournalActivity : AppCompatActivity() {
    private lateinit var etJournal: EditText
    private lateinit var tvName: TextView
    private lateinit var tvCharCount: TextView
    private lateinit var btnSaveJournal: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_journal)

        etJournal = findViewById(R.id.et_journal)
        tvName = findViewById(R.id.tv_name_journal)
        tvCharCount = findViewById(R.id.tv_char_count)
        btnSaveJournal = findViewById(R.id.btn_save_journal)

        tvName.text = intent.getStringExtra(getString(R.string.NICKNAME_KEY))

        etJournal.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = 500 - s!!.length
                tvCharCount.text = length.toString()

                btnSaveJournal.isEnabled = !s.isEmpty()
            }
        })

        btnSaveJournal.isEnabled = false
        btnSaveJournal.setOnClickListener {
            val resultIntent = Intent()

            resultIntent.putExtra(getString(R.string.JOURNAL_KEY), etJournal.text.toString())
            setResult(Activity.RESULT_OK, resultIntent)

            finish()
        }
    }
}