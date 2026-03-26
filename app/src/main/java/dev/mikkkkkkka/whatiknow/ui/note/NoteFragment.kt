package dev.mikkkkkkka.whatiknow.ui.note

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.mikkkkkkka.whatiknow.R
import dev.mikkkkkkka.whatiknow.databinding.FragmentNoteBinding
import dev.mikkkkkkka.whatiknow.ui.workspace.WorkspaceActivity

@AndroidEntryPoint
class NoteFragment : Fragment(R.layout.fragment_note) {

    private var binding: FragmentNoteBinding? = null
    private lateinit var viewModel: NoteViewModel
    private var isApplyingState = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNoteBinding.bind(view)
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        setupEditor()
        observeViewModel()

        viewModel.loadNote(arguments?.getString(WorkspaceActivity.EXTRA_NOTE_ID))
    }

    override fun onPause() {
        saveContentImmediately()
        super.onPause()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setupEditor() {
        binding?.noteEditText?.doAfterTextChanged { editable ->
            if (isApplyingState) return@doAfterTextChanged
            viewModel.onContentChanged(editable?.toString().orEmpty())
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            val currentBinding = binding ?: return@observe
            currentBinding.titleTextView.text = state.title

            val currentText = currentBinding.noteEditText.text?.toString().orEmpty()
            if (currentText != state.content) {
                isApplyingState = true
                currentBinding.noteEditText.setText(state.content)
                currentBinding.noteEditText.setSelection(currentBinding.noteEditText.text?.length ?: 0)
                isApplyingState = false
            }
        }
    }

    fun saveContentImmediately() {
        if (!::viewModel.isInitialized) return
        viewModel.saveImmediately(binding?.noteEditText?.text?.toString().orEmpty())
    }

    companion object {
        fun create(noteId: String?): NoteFragment {
            return NoteFragment().apply {
                arguments = Bundle().apply {
                    putString(WorkspaceActivity.EXTRA_NOTE_ID, noteId)
                }
            }
        }
    }
}
