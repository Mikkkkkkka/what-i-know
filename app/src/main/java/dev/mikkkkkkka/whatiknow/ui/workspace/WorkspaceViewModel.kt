package dev.mikkkkkkka.whatiknow.ui.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikkkkkkka.whatiknow.data.remote.BduiNote
import dev.mikkkkkkka.whatiknow.data.remote.BduiRepository
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
    val homeTemplate: JsonObject = defaultHomeTemplate(),
    val editorTemplate: JsonObject = defaultEditorTemplate(),
    val templateSource: String = "embedded",
    val notesSource: String = "cache",
    val syncState: String = "booting",
    val message: String? = null,
    val selectedNoteId: String? = null,
    val draftTitle: String = "",
    val draftBody: String = "",
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

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, syncState = "syncing", message = null)
            }

            repository.seedTemplatesIfMissing(
                homeTemplate = defaultHomeTemplate(),
                editorTemplate = defaultEditorTemplate(),
            )

            val templates = repository.loadTemplates(
                homeFallback = defaultHomeTemplate(),
                editorFallback = defaultEditorTemplate(),
            )
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

    fun onAction(action: String, noteId: String? = null) {
        when (action) {
            "create_note" -> openEditor(null)
            "refresh" -> refresh()
            "back" -> goHome()
            "save_note" -> saveNote()
            "delete_note" -> deleteNote()
            "open_note" -> openEditor(noteId)
        }
    }

    fun onDraftTitleChange(value: String) {
        _uiState.update { it.copy(draftTitle = value) }
    }

    fun onDraftBodyChange(value: String) {
        _uiState.update { it.copy(draftBody = value) }
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
            )
        }
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
                    notes = nextNotes,
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
                    notes = nextNotes,
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

private fun defaultHomeTemplate(): JsonObject {
    return jsonObject(
        "title" to "Knowledge Cloud",
        "subtitle" to "Backend-driven home screen",
        "components" to JsonArray().apply {
            add(
                jsonObject(
                    "type" to "hero",
                    "eyebrow" to "BDUI",
                    "title" to "Cloud notes from JSON templates",
                    "body" to "The screen composition comes from Alfa Echo. The client resolves actions and binds note data locally."
                )
            )
            add(
                jsonObject(
                    "type" to "stats",
                    "items" to JsonArray().apply {
                        add(jsonObject("label" to "Notes", "value" to "{notesCount}"))
                        add(jsonObject("label" to "Templates", "value" to "{templateSource}"))
                        add(jsonObject("label" to "Sync", "value" to "{syncState}"))
                    }
                )
            )
            add(
                jsonObject(
                    "type" to "actions",
                    "items" to JsonArray().apply {
                        add(jsonObject("label" to "New note", "action" to "create_note"))
                        add(jsonObject("label" to "Refresh", "action" to "refresh"))
                    }
                )
            )
            add(
                jsonObject(
                    "type" to "note_list",
                    "title" to "Saved notes",
                    "empty_title" to "No notes yet",
                    "empty_body" to "Create the first note. The collection is stored in Echo API under a device-specific namespace."
                )
            )
        }
    )
}

private fun defaultEditorTemplate(): JsonObject {
    return jsonObject(
        "title" to "Editor",
        "subtitle" to "Backend-driven editor screen",
        "components" to JsonArray().apply {
            add(
                jsonObject(
                    "type" to "hero",
                    "eyebrow" to "Document",
                    "title" to "{editorModeTitle}",
                    "body" to "Fields are native Compose components, but order, copy and actions are delivered through the template."
                )
            )
            add(
                jsonObject(
                    "type" to "editor",
                    "title_label" to "Title",
                    "title_hint" to "What did you learn today?",
                    "body_label" to "Body",
                    "body_hint" to "Capture the note, decision or result.",
                    "save_label" to "Save to cloud",
                    "delete_label" to "Delete note"
                )
            )
            add(
                jsonObject(
                    "type" to "actions",
                    "items" to JsonArray().apply {
                        add(jsonObject("label" to "Back", "action" to "back"))
                        add(jsonObject("label" to "Refresh template", "action" to "refresh"))
                    }
                )
            )
        }
    )
}

private fun jsonObject(vararg pairs: Pair<String, Any?>): JsonObject {
    return JsonObject().apply {
        pairs.forEach { (key, value) ->
            when (value) {
                null -> add(key, JsonNull.INSTANCE)
                is String -> addProperty(key, value)
                is Number -> addProperty(key, value)
                is Boolean -> addProperty(key, value)
                is JsonElement -> add(key, value)
            }
        }
    }
}
