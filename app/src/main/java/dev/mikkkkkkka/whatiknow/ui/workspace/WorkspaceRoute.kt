package dev.mikkkkkkka.whatiknow.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.mikkkkkkka.whatiknow.data.remote.BduiNote

@Composable
fun WorkspaceRoute(
    viewModel: WorkspaceViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    WorkspaceScreen(
        state = state,
        onAction = viewModel::onAction,
        onTitleChange = viewModel::onDraftTitleChange,
        onBodyChange = viewModel::onDraftBodyChange,
    )
}

@Composable
private fun WorkspaceScreen(
    state: WorkspaceUiState,
    onAction: (String, String?) -> Unit,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
) {
    val template = if (state.route == WorkspaceDestination.EDITOR) state.editorTemplate else state.homeTemplate
    val title = template.string("title").resolve(state)
    val subtitle = template.string("subtitle").resolve(state)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .safeDrawingPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (state.message != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = state.message,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            RenderBduiComponents(
                state = state,
                template = template,
                onAction = onAction,
                onTitleChange = onTitleChange,
                onBodyChange = onBodyChange,
            )
        }
    }
}

@Composable
private fun RenderBduiComponents(
    state: WorkspaceUiState,
    template: JsonObject,
    onAction: (String, String?) -> Unit,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
) {
    val components = template.array("components")
    if (state.route == WorkspaceDestination.HOME) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(components.size()) { index ->
                val component = components[index]
                RenderComponent(
                    component = component.asJsonObject,
                    state = state,
                    onAction = onAction,
                    onTitleChange = onTitleChange,
                    onBodyChange = onBodyChange,
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            components.forEach { component ->
                RenderComponent(
                    component = component.asJsonObject,
                    state = state,
                    onAction = onAction,
                    onTitleChange = onTitleChange,
                    onBodyChange = onBodyChange,
                )
            }
        }
    }
}

@Composable
private fun RenderComponent(
    component: JsonObject,
    state: WorkspaceUiState,
    onAction: (String, String?) -> Unit,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
) {
    when (component.string("type")) {
        "hero" -> HeroCard(component, state)
        "stats" -> StatsRow(component, state)
        "actions" -> ActionRow(component, state, onAction)
        "note_list" -> NotesList(component, state, onAction)
        "editor" -> EditorCard(component, state, onAction, onTitleChange, onBodyChange)
    }
}

@Composable
private fun HeroCard(component: JsonObject, state: WorkspaceUiState) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = component.string("eyebrow").resolve(state).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = component.string("title").resolve(state),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = component.string("body").resolve(state),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatsRow(component: JsonObject, state: WorkspaceUiState) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        component.array("items").forEach { item ->
            val stat = item.asJsonObject
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.width(110.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = stat.string("label"),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stat.string("value").resolve(state),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActionRow(
    component: JsonObject,
    state: WorkspaceUiState,
    onAction: (String, String?) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        component.array("items").forEach { item ->
            val action = item.asJsonObject
            TextButton(
                onClick = { onAction(action.string("action"), null) },
            ) {
                Text(
                    text = action.string("label").resolve(state),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun NotesList(
    component: JsonObject,
    state: WorkspaceUiState,
    onAction: (String, String?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = component.string("title").resolve(state),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        if (state.notes.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = component.string("empty_title").resolve(state),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = component.string("empty_body").resolve(state),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.notes.forEach { note ->
                    NoteCard(note = note) {
                        onAction("open_note", note.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteCard(
    note: BduiNote,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = note.content.ifBlank { "Open to add details" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = note.updatedAt,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun EditorCard(
    component: JsonObject,
    state: WorkspaceUiState,
    onAction: (String, String?) -> Unit,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = component.string("title_label"),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            OutlinedTextField(
                value = state.draftTitle,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(component.string("title_hint"))
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                shape = RoundedCornerShape(20.dp),
            )
            Text(
                text = component.string("body_label"),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            OutlinedTextField(
                value = state.draftBody,
                onValueChange = onBodyChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                placeholder = {
                    Text(component.string("body_hint"))
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                shape = RoundedCornerShape(20.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onAction("save_note", null) },
                ) {
                    Text(component.string("save_label"))
                }
                if (state.selectedNoteId != null) {
                    TextButton(
                        onClick = { onAction("delete_note", null) },
                    ) {
                        Text(component.string("delete_label"))
                    }
                }
            }
        }
    }
}

private fun JsonObject.string(key: String): String {
    return get(key)?.takeIf { !it.isJsonNull }?.asString.orEmpty()
}

private fun JsonObject.array(key: String): JsonArray {
    return getAsJsonArray(key) ?: JsonArray()
}

private fun String.resolve(state: WorkspaceUiState): String {
    return this
        .replace("{notesCount}", state.notes.size.toString())
        .replace("{templateSource}", state.templateSource)
        .replace("{syncState}", state.syncState)
        .replace("{editorModeTitle}", if (state.selectedNoteId == null) "Create note" else "Edit note")
}
