package dev.mikkkkkkka.whatiknow.ui.mark

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.mikkkkkkka.whatiknow.R
import dev.mikkkkkkka.whatiknow.databinding.ActivityMarkBinding
import dev.mikkkkkkka.whatiknow.ui.workspace.WorkspaceActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        closeCalendar()
        binding.dateTextView.setOnClickListener { toggleCalendar() }
        binding.workspaceButton.setOnClickListener { startWorkspaceActivity() }
    }

    private fun toggleCalendar() {
        when (binding.dateSelectCalendarView.visibility) {
            View.GONE -> openCalendar()
            else -> closeCalendar()
        }
    }

    private fun openCalendar() {
        binding.dateSelectCalendarView.visibility = View.VISIBLE
    }

    private fun closeCalendar() {
        binding.dateSelectCalendarView.visibility = View.GONE
    }

    private fun incrementDate() {
        val text = binding.dateTextView.text
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val currentDate = LocalDate.parse(text, formatter)
        val newDate = currentDate.plusDays(1)
        binding.dateTextView.text = newDate.format(formatter)
    }

    private fun startWorkspaceActivity() {
        if (callingActivity?.className == WorkspaceActivity.toString()) {
            startActivity(WorkspaceActivity.createIntent(this))
        } else {
            finish()
        }
    }
}
