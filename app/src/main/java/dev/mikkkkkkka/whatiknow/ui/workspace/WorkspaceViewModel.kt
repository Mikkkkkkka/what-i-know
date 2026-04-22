package dev.mikkkkkkka.whatiknow.ui.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikkkkkkka.whatiknow.data.remote.BduiNote
import dev.mikkkkkkka.whatiknow.data.remote.BduiRepository
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiAction
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiActionKind
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiDestination
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiOperation
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiScreen
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class WorkspaceDestination {
    HOME,
    EDITOR,
}

data class WorkspaceUiState(
    val route: WorkspaceDestination = WorkspaceDestination.HOME,
    val isLoading: Boolean = true,
    val notes: List<BduiNote> = emptyList(),
    val homeTemplate: BduiScreen? = null,
    val editorTemplate: BduiScreen? = null,
    val templateSource: String = "booting",
    val notesSource: String = "cache",
    val syncState: String = "booting",
    val message: String? = null,
    val selectedNoteId: String? = null,
    val draftTitle: String = "",
    val draftBody: String = "",
    val externalNavigation: BduiDestination? = null,
)

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val repository: BduiRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkspaceUiState())
    val uiState: StateFlow<WorkspaceUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun openNoteFromIntent(noteId: String?) {
        if (noteId.isNullOrBlank()) {
            return
        }
        openEditor(noteId)
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, syncState = "syncing", message = null)
            }

            repository.seedTemplatesIfMissing()
            val templates = repository.loadTemplates()
            val noteSnapshot = repository.loadNotes()

            _uiState.update { current ->
                val selected = noteSnapshot.notes.firstOrNull { note ->
                    note.id == current.selectedNoteId
                }
                current.copy(
                    isLoading = false,
                    notes = noteSnapshot.notes,
                    homeTemplate = templates.home,
                    editorTemplate = templates.editor,
                    templateSource = templates.source,
                    notesSource = noteSnapshot.source,
                    syncState = if (noteSnapshot.fromRemote) "ready" else "offline",
                    message = noteSnapshot.message,
                    draftTitle = selected?.title ?: current.draftTitle,
                    draftBody = selected?.content ?: current.draftBody,
                )
            }
        }
    }

    fun onAction(action: BduiAction, noteId: String? = null) {
        when (action.kind) {
            BduiActionKind.NAVIGATE -> handleNavigation(action, noteId)
            BduiActionKind.COMMAND -> handleCommand(action.operation)
        }
    }

    fun onDraftTitleChange(value: String) {
        _uiState.update { it.copy(draftTitle = value) }
    }

    fun onDraftBodyChange(value: String) {
        _uiState.update { it.copy(draftBody = value) }
    }

    private fun handleNavigation(action: BduiAction, clickedNoteId: String?) {
        when (action.destination) {
            BduiDestination.HOME -> goHome()
            BduiDestination.EDITOR -> {
                val resolvedNoteId = resolveNoteIdBinding(action.noteIdBinding, clickedNoteId)
                openEditor(resolvedNoteId)
            }
            BduiDestination.MARK, BduiDestination.AUTH -> {
                _uiState.update { it.copy(externalNavigation = action.destination) }
            }
            BduiDestination.WORKSPACE -> Unit

            null -> Unit
        }
    }

    private fun handleCommand(operation: BduiOperation?) {
        when (operation) {
            BduiOperation.REFRESH -> refresh()
            BduiOperation.SAVE_NOTE -> saveNote()
            BduiOperation.DELETE_NOTE -> deleteNote()
            else -> Unit
        }
    }

    private fun resolveNoteIdBinding(binding: String?, clickedNoteId: String?): String? {
        return when (binding) {
            null, "" -> clickedNoteId
            "{noteId}" -> clickedNoteId
            "{selectedNoteId}" -> _uiState.value.selectedNoteId
            else -> binding
        }
    }

    private fun openEditor(noteId: String?) {
        val note = _uiState.value.notes.firstOrNull { it.id == noteId }
        _uiState.update {
            it.copy(
                route = WorkspaceDestination.EDITOR,
                selectedNoteId = noteId,
                draftTitle = note?.title.orEmpty(),
                draftBody = note?.content.orEmpty(),
                message = null,
                externalNavigation = null,
            )
        }
    }

    private fun goHome() {
        _uiState.update {
            it.copy(
                route = WorkspaceDestination.HOME,
                selectedNoteId = null,
                draftTitle = "",
                draftBody = "",
                message = null,
                externalNavigation = null,
            )
        }
    }

    fun onExternalNavigationHandled() {
        _uiState.update { it.copy(externalNavigation = null) }
    }

    private fun saveNote() {
        val state = _uiState.value
        val title = state.draftTitle.trim()
        val body = state.draftBody.trim()
        if (title.isEmpty() && body.isEmpty()) {
            _uiState.update { it.copy(message = "Note is empty") }
            return
        }

        viewModelScope.launch {
            val timestamp = OffsetDateTime.now(ZoneOffset.UTC).toString()
            val updated = BduiNote(
                id = state.selectedNoteId ?: UUID.randomUUID().toString(),
                title = title.ifEmpty { "Untitled note" },
                content = body,
                updatedAt = timestamp,
            )
            val nextNotes = state.notes
                .filterNot { it.id == updated.id }
                .plus(updated)
                .sortedByDescending { it.updatedAt }

            val result = repository.saveNotes(nextNotes)
            _uiState.update {
                it.copy(
                    route = WorkspaceDestination.HOME,
                    notes = result.notes,
                    selectedNoteId = null,
                    draftTitle = "",
                    draftBody = "",
                    notesSource = result.source,
                    syncState = if (result.fromRemote) "ready" else "offline",
                    message = result.message ?: "Note saved",
                )
            }
        }
    }

    private fun deleteNote() {
        val noteId = _uiState.value.selectedNoteId ?: return
        viewModelScope.launch {
            val nextNotes = _uiState.value.notes.filterNot { it.id == noteId }
            val result = repository.saveNotes(nextNotes)
            _uiState.update {
                it.copy(
                    route = WorkspaceDestination.HOME,
                    notes = result.notes,
                    selectedNoteId = null,
                    draftTitle = "",
                    draftBody = "",
                    notesSource = result.source,
                    syncState = if (result.fromRemote) "ready" else "offline",
                    message = result.message ?: "Note deleted",
                )
            }
        }
    }
}
