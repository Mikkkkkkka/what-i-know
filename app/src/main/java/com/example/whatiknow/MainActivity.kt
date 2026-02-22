package com.example.whatiknow

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    var date: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomInset = maxOf(systemBars.bottom, ime.bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomInset)
            insets
        }

        date = findViewById(R.id.dateTextView)
        date?.setOnClickListener { incrementDate() }
    }

    private fun openCalendar() {}

    private fun incrementDate() {
        val text = date?.text?.toString() ?: return
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val currentDate = LocalDate.parse(text, formatter)
        val newDate = currentDate.plusDays(1)
        date?.text = newDate.format(formatter)
    }
}