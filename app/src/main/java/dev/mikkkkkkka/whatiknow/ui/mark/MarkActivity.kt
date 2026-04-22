package dev.mikkkkkkka.whatiknow.ui.mark

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.mikkkkkkka.whatiknow.R
import dev.mikkkkkkka.whatiknow.databinding.ActivityMarkBinding
import dev.mikkkkkkka.whatiknow.ui.workspace.WorkspaceActivity
import java.time.format.DateTimeFormatter
import java.time.YearMonth

@AndroidEntryPoint
class MarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMarkBinding
    private lateinit var viewModel: MarkViewModel
    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private var isApplyingState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MarkViewModel::class.java]
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupEditor()
        observeViewModel()
        closeCalendar()
        binding.dateTextView.setOnClickListener { toggleCalendar() }
        binding.workspaceButton.setOnClickListener { startWorkspaceActivity() }
        viewModel.loadInitialDate()
    }

    override fun onPause() {
        if (::viewModel.isInitialized) {
            viewModel.saveImmediately(binding.noteEditText.text?.toString().orEmpty())
        }
        super.onPause()
    }

    private fun setupEditor() {
        binding.noteEditText.doAfterTextChanged { editable ->
            if (isApplyingState) return@doAfterTextChanged
            viewModel.onContentChanged(editable?.toString().orEmpty())
        }

        binding.calendarHeatmapView.onDateClick = { date ->
            viewModel.selectDate(date)
            closeCalendar()
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            binding.dateTextView.text = state.selectedDate.format(dateFormatter)
            binding.calendarHeatmapView.setMonth(YearMonth.from(state.selectedDate))
            binding.calendarHeatmapView.setSelectedDate(state.selectedDate)
            binding.calendarHeatmapView.setData(state.heatmapValues)

            val currentText = binding.noteEditText.text?.toString().orEmpty()
            if (currentText != state.content) {
                isApplyingState = true
                binding.noteEditText.setText(state.content)
                binding.noteEditText.setSelection(binding.noteEditText.text?.length ?: 0)
                isApplyingState = false
            }
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
}
