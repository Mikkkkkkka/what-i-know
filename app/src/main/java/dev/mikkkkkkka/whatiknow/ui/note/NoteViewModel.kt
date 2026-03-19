package dev.mikkkkkkka.whatiknow.ui.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikkkkkkka.whatiknow.domain.model.Note
import dev.mikkkkkkka.whatiknow.domain.usecase.note.DeleteNoteUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.note.GetNoteUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.note.SaveNoteUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val getNote: GetNoteUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
) : ViewModel() {

    private val innerState = MutableLiveData(NoteEditorState())
    val state: LiveData<NoteEditorState> = innerState

    private var autosaveJob: Job? = null

    fun loadNote(noteId: String?) {
        if (noteId.isNullOrBlank()) {
            innerState.value = NoteEditorState()
            return
        }

        viewModelScope.launch {
            val note = getNote(noteId)
            innerState.value = if (note == null) {
                NoteEditorState()
            } else {
                NoteEditorState(
                    noteId = note.id,
                    title = note.content.toNoteTitle(),
                    content = note.content,
                    isExistingNote = true,
                )
            }
        }
    }

    fun onContentChanged(content: String) {
        val currentState = innerState.value ?: NoteEditorState()
        innerState.value = currentState.copy(
            content = content,
            title = content.toNoteTitle(),
        )

        autosaveJob?.cancel()
        autosaveJob = viewModelScope.launch {
            delay(AUTOSAVE_DELAY_MS)
            persistContent(content)
        }
    }

    fun saveImmediately(content: String) {
        autosaveJob?.cancel()
        viewModelScope.launch {
            persistContent(content)
        }
    }

    private suspend fun persistContent(content: String) {
        val currentState = innerState.value ?: NoteEditorState()
        val normalizedContent = content.trimEnd()

        if (normalizedContent.isBlank()) {
            currentState.noteId?.let { deleteNoteUseCase(it) }
            innerState.value = NoteEditorState()
            return
        }

        val noteId = currentState.noteId ?: UUID.randomUUID().toString()
        saveNoteUseCase(
            Note(
                id = noteId,
                content = normalizedContent,
            )
        )

        innerState.value = currentState.copy(
            noteId = noteId,
            title = normalizedContent.toNoteTitle(),
            content = normalizedContent,
            isExistingNote = true,
        )
    }

    companion object {
        private const val AUTOSAVE_DELAY_MS = 300L
    }
}

data class NoteEditorState(
    val noteId: String? = null,
    val title: String = "New Note",
    val content: String = "",
    val isExistingNote: Boolean = false,
)

private fun String.toNoteTitle(): String {
    val firstNonEmptyLine = lineSequence()
        .map { it.trim() }
        .firstOrNull { it.isNotEmpty() }

    return firstNonEmptyLine?.take(TITLE_MAX_LENGTH) ?: "New Note"
}

private const val TITLE_MAX_LENGTH = 15
