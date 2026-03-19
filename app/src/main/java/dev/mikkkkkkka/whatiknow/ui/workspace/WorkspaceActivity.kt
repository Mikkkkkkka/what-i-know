package dev.mikkkkkkka.whatiknow.ui.workspace

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dev.mikkkkkkka.whatiknow.R
import dev.mikkkkkkka.whatiknow.databinding.ActivityWorkspaceBinding
import dev.mikkkkkkka.whatiknow.di.impl.AppModuleImpl
import dev.mikkkkkkka.whatiknow.di.impl.DomainModuleImpl
import dev.mikkkkkkka.whatiknow.ui.note.NoteActivity

class WorkspaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkspaceBinding
    private lateinit var adapter: NotesAdapter

    private val viewModel: WorkspaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkspaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecycler()
        setupListeners()
        observeViewModel()

        viewModel.loadNotes()
    }

    private fun setupRecycler() {
        adapter = NotesAdapter { noteId ->
            openNote(noteId)
        }

        binding.notesRecycler.layoutManager =
            LinearLayoutManager(this)

        binding.notesRecycler.adapter = adapter
    }

    private fun setupListeners() {
        binding.newButton.setOnClickListener {
            createNote()
        }
    }

    private fun observeViewModel() {
        viewModel.notes.observe(this) { notes ->
            adapter.submitList(notes)
        }
    }

    private fun openNote(noteId: String) {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra("note_id", noteId)

        startActivity(intent)
    }

    private fun createNote() {
        val intent = Intent(this, NoteActivity::class.java)
        startActivity(intent)
    }
}