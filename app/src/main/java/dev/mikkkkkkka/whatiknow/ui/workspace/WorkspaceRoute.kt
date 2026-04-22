package dev.mikkkkkkka.whatiknow.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mikkkkkkka.whatiknow.data.remote.BduiNote
import dev.mikkkkkkka.whatiknow.data.remote.bdui.ActionItem
import dev.mikkkkkkka.whatiknow.data.remote.bdui.ActionsComponent
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiAction
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiComponent
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiDestination
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiScreen
import dev.mikkkkkkka.whatiknow.data.remote.bdui.EditorComponent
import dev.mikkkkkkka.whatiknow.data.remote.bdui.HeroComponent
import dev.mikkkkkkka.whatiknow.data.remote.bdui.NoteListComponent
import dev.mikkkkkkka.whatiknow.data.remote.bdui.StatsComponent

@Composable
fun WorkspaceRoute(
    viewModel: WorkspaceViewModel,
    onOpenMark: () -> Unit = {},
    onOpenAuth: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.externalNavigation) {
        when (state.externalNavigation) {
            BduiDestination.MARK -> {
                onOpenMark()
                viewModel.onExternalNavigationHandled()
            }
            BduiDestination.AUTH -> {
                onOpenAuth()
                viewModel.onExternalNavigationHandled()
            }
            else -> Unit
        }
    }
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
    onAction: (BduiAction, String?) -> Unit,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
) {
    val template = when (state.route) {
        WorkspaceDestination.HOME -> state.homeTemplate
        WorkspaceDestination.EDITOR -> state.editorTemplate
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        if (template == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .safeDrawingPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .safeDrawingPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Text(
                text = template.title.resolve(state),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            if (template.subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = template.subtitle.resolve(state),
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
    template: BduiScreen,
    onAction: (BduiAction, String?) -> Unit,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
) {
    if (state.route == WorkspaceDestination.HOME) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(template.components, key = { it.id }) { component ->
                RenderComponent(
                    component = component,
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
            template.components.forEach { component ->
                RenderComponent(
                    component = component,
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
    component: BduiComponent,
    state: WorkspaceUiState,
    onAction: (BduiAction, String?) -> Unit,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
) {
    when (component) {
        is HeroComponent -> HeroCard(component, state)
        is StatsComponent -> StatsRow(component, state)
        is ActionsComponent -> ActionRow(component, state, onAction)
        is NoteListComponent -> NotesList(component, state, onAction)
        is EditorComponent -> EditorCard(component, state, onAction, onTitleChange, onBodyChange)
        else -> Unit
    }
}

@Composable
private fun HeroCard(component: HeroComponent, state: WorkspaceUiState) {
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
                text = component.eyebrow.resolve(state).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = component.title.resolve(state),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = component.body.resolve(state),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatsRow(component: StatsComponent, state: WorkspaceUiState) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        component.items.forEach { stat ->
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
                        text = stat.label.resolve(state),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stat.value.resolve(state),
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
    component: ActionsComponent,
    state: WorkspaceUiState,
    onAction: (BduiAction, String?) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        component.items.forEach { action ->
            TextButton(
                onClick = { onAction(action.action, null) },
            ) {
                Text(
                    text = action.label.resolve(state),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun NotesList(
    component: NoteListComponent,
    state: WorkspaceUiState,
    onAction: (BduiAction, String?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = component.title.resolve(state),
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
                        text = component.emptyTitle.resolve(state),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = component.emptyBody.resolve(state),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.notes.forEach { note ->
                    NoteCard(note = note) {
                        onAction(component.itemAction, note.id)
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
    component: EditorComponent,
    state: WorkspaceUiState,
    onAction: (BduiAction, String?) -> Unit,
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
                text = component.titleLabel.resolve(state),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            OutlinedTextField(
                value = state.draftTitle,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(component.titleHint.resolve(state))
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                shape = RoundedCornerShape(20.dp),
            )
            Text(
                text = component.bodyLabel.resolve(state),
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
                    Text(component.bodyHint.resolve(state))
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                shape = RoundedCornerShape(20.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionButton(action = component.primaryAction, state = state, onAction = onAction)
                if (state.selectedNoteId != null) {
                    component.secondaryAction?.let { secondary ->
                        TextButton(
                            onClick = { onAction(secondary.action, null) },
                        ) {
                            Text(secondary.label.resolve(state))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    action: ActionItem,
    state: WorkspaceUiState,
    onAction: (BduiAction, String?) -> Unit,
) {
    Button(
        onClick = { onAction(action.action, null) },
    ) {
        Text(action.label.resolve(state))
    }
}

private fun String.resolve(state: WorkspaceUiState): String {
    return this
        .replace("{notesCount}", state.notes.size.toString())
        .replace("{templateSource}", state.templateSource)
        .replace("{syncState}", state.syncState)
        .replace("{editorModeTitle}", if (state.selectedNoteId == null) "Create note" else "Edit note")
}
