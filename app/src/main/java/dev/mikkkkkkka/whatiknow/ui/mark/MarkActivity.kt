package dev.mikkkkkkka.whatiknow.ui.mark

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.mikkkkkkka.whatiknow.R
import dev.mikkkkkkka.whatiknow.databinding.ActivityMarkBinding
import dev.mikkkkkkka.whatiknow.ui.workspace.WorkspaceActivity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.YearMonth

class MarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMarkBinding
    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private var selectedDate: LocalDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindCalendar()
        closeCalendar()
        binding.dateTextView.setOnClickListener { toggleCalendar() }
        binding.workspaceButton.setOnClickListener { startWorkspaceActivity() }
    }

    private fun bindCalendar() {
        binding.dateTextView.text = selectedDate.format(dateFormatter)
        binding.calendarHeatmapView.setMonth(YearMonth.from(selectedDate))
        binding.calendarHeatmapView.setSelectedDate(selectedDate)
        binding.calendarHeatmapView.setData(buildPreviewHeatmapData(YearMonth.from(selectedDate)))
        binding.calendarHeatmapView.onDateClick = { date ->
            selectedDate = date
            binding.dateTextView.text = selectedDate.format(dateFormatter)
            closeCalendar()
        }
    }

    private fun toggleCalendar() {
        when (binding.calendarHeatmapView.visibility) {
            View.GONE -> openCalendar()
            else -> closeCalendar()
        }
    }

    private fun openCalendar() {
        binding.calendarHeatmapView.visibility = View.VISIBLE
    }

    private fun closeCalendar() {
        binding.calendarHeatmapView.visibility = View.GONE
    }

    private fun startWorkspaceActivity() {
        if (callingActivity?.className == WorkspaceActivity.toString()) {
            startActivity(WorkspaceActivity.createIntent(this))
        } else {
            finish()
        }
    }

    private fun buildPreviewHeatmapData(month: YearMonth): Map<LocalDate, Int> {
        val values = mutableMapOf<LocalDate, Int>()
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            val count = when {
                date == LocalDate.now() -> 4
                date.dayOfWeek == DayOfWeek.MONDAY -> 2
                date.dayOfWeek == DayOfWeek.FRIDAY -> 3
                day % 5 == 0 -> 1
                else -> 0
            }
            if (count > 0) {
                values[date] = count
            }
        }
        return values
    }
}
