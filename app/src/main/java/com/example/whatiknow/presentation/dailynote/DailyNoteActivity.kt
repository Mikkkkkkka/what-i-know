package com.example.whatiknow.presentation.dailynote

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.whatiknow.presentation.workspacenote.WorkspaceNoteActivity
import com.example.whatiknow.R
import com.example.whatiknow.databinding.ActivityDailyNoteBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDailyNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityDailyNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomInset = maxOf(systemBars.bottom, ime.bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomInset)
            insets
        }

        binding.dateTextView.setOnClickListener { incrementDate() }
        binding.newButton.setOnClickListener { startNewNoteActivity() }
    }

    private fun openCalendar() {}

    private fun incrementDate() {
        val text = binding.dateTextView.text
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val currentDate = LocalDate.parse(text, formatter)
        val newDate = currentDate.plusDays(1)
        binding.dateTextView.text = newDate.format(formatter)
    }

    private fun startNewNoteActivity() {
        val intent = Intent(this, WorkspaceNoteActivity::class.java)
        startActivity(intent)
    }
}