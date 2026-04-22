package dev.mikkkkkkka.whatiknow.ui.workspace

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.mikkkkkkka.whatiknow.R
import dev.mikkkkkkka.whatiknow.databinding.FragmentWorkspaceBinding

@AndroidEntryPoint
class WorkspaceFragment : Fragment(R.layout.fragment_workspace) {

    interface Callbacks {
        fun onOpenNote(noteId: String)
        fun onCreateNote()
        fun onOpenMarks()
        fun onSync()
    }

    private var callbacks: Callbacks? = null
    private var binding: FragmentWorkspaceBinding? = null
    private lateinit var adapter: NotesAdapter
    private lateinit var viewModel: WorkspaceViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as? Callbacks
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWorkspaceBinding.bind(view)
        viewModel = ViewModelProvider(this)[WorkspaceViewModel::class.java]

        setupRecycler()
        setupListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onDetach() {
        callbacks = null
        super.onDetach()
    }

    private fun setupRecycler() {
        adapter = NotesAdapter { noteId ->
            callbacks?.onOpenNote(noteId)
        }

        binding?.notesRecycler?.layoutManager = LinearLayoutManager(requireContext())
        binding?.notesRecycler?.adapter = adapter
    }

    private fun setupListeners() {
        binding?.newButton?.setOnClickListener {
            callbacks?.onCreateNote()
        }
        binding?.marksButton?.setOnClickListener {
            callbacks?.onOpenMarks()
        }
        binding?.syncButton?.setOnClickListener {
            callbacks?.onSync()
        }
    }

    private fun observeViewModel() {
        adapter.submitList(emptyList())
    }
}
