package dev.mikkkkkkka.whatiknow.ui.note

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.mikkkkkkka.whatiknow.WhatIKnowApplication
import dev.mikkkkkkka.whatiknow.databinding.ActivityNoteBinding

class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding

    private val viewModel: NoteViewModel by viewModels {
        NoteViewModel.factory(WhatIKnowApplication.appModule)
    }

    private var isApplyingState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupEditor()
        observeViewModel()

        viewModel.loadNote(intent.getStringExtra(EXTRA_NOTE_ID))
    }

    override fun onPause() {
        viewModel.saveImmediately(binding.noteEditText.text?.toString().orEmpty())
        super.onPause()
    }

    private fun setupEditor() {
        binding.noteEditText.doAfterTextChanged { editable ->
            if (isApplyingState) return@doAfterTextChanged
            viewModel.onContentChanged(editable?.toString().orEmpty())
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            binding.titleTextView.text = state.title

            val currentText = binding.noteEditText.text?.toString().orEmpty()
            if (currentText != state.content) {
                isApplyingState = true
                binding.noteEditText.setText(state.content)
                binding.noteEditText.setSelection(binding.noteEditText.text?.length ?: 0)
                isApplyingState = false
            }
        }
    }

    companion object {
        const val EXTRA_NOTE_ID = "note_id"
    }
}
