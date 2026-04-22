package dev.mikkkkkkka.whatiknow.ui.mark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mikkkkkkka.whatiknow.data.remote.bdui.ActionsComponent
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiAction
import dev.mikkkkkkka.whatiknow.data.remote.bdui.HeroComponent
import dev.mikkkkkkka.whatiknow.data.remote.bdui.MarkCalendarComponent
import dev.mikkkkkkka.whatiknow.data.remote.bdui.MarkEditorComponent
import java.time.LocalDate

@Composable
fun MarkRoute(
    viewModel: MarkViewModel,
    onOpenWorkspace: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.navigateToWorkspace) {
        if (state.navigateToWorkspace) {
            onOpenWorkspace()
            viewModel.onNavigationHandled()
        }
    }

    MarkScreen(
        state = state,
        onAction = viewModel::onAction,
        onBodyChange = viewModel::onBodyChange,
    )
}

@Composable
private fun MarkScreen(
    state: MarkUiState,
    onAction: (BduiAction, String?) -> Unit,
    onBodyChange: (String) -> Unit,
) {
    val screen = state.screen
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        if (screen == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .safeDrawingPadding()
                    .navigationBarsPadding(),
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = screen.title.resolve(state),
                style = MaterialTheme.typography.headlineLarge,
            )
            if (screen.subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = screen.subtitle.resolve(state),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (!state.message.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(
                        text = state.message.orEmpty(),
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            screen.components.forEach { component ->
                when (component) {
                    is HeroComponent -> MarkHero(component, state)
                    is MarkCalendarComponent -> MarkCalendar(component, state, onAction)
                    is MarkEditorComponent -> MarkEditor(component, state, onAction, onBodyChange)
                    is ActionsComponent -> MarkActions(component, state, onAction)
                    else -> Unit
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MarkHero(component: HeroComponent, state: MarkUiState) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = component.eyebrow.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(text = component.title.resolve(state), style = MaterialTheme.typography.headlineMedium)
            Text(
                text = component.body.resolve(state),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MarkCalendar(
    component: MarkCalendarComponent,
    state: MarkUiState,
    onAction: (BduiAction, String?) -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = component.title.resolve(state), style = MaterialTheme.typography.titleLarge)
            if (component.subtitle.isNotBlank()) {
                Text(
                    text = component.subtitle.resolve(state),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = state.selectedDate.format(MarkViewModel.UiDateFormatter),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    CalendarHeatmapView(context).apply {
                        onDateClick = { date ->
                            onAction(component.selectAction, date.toString())
                        }
                    }
                },
                update = { view ->
                    view.setMonth(java.time.YearMonth.from(state.selectedDate))
                    view.setSelectedDate(state.selectedDate)
                    view.setData(state.heatmapValues)
                },
            )
        }
    }
}

@Composable
private fun MarkEditor(
    component: MarkEditorComponent,
    state: MarkUiState,
    onAction: (BduiAction, String?) -> Unit,
    onBodyChange: (String) -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = component.bodyLabel.resolve(state),
                style = MaterialTheme.typography.labelLarge,
            )
            OutlinedTextField(
                value = state.content,
                onValueChange = onBodyChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                placeholder = { Text(component.bodyHint.resolve(state)) },
            )
            Button(onClick = { onAction(component.primaryAction.action, null) }) {
                Text(component.primaryAction.label.resolve(state))
            }
            component.secondaryAction?.let { secondary ->
                TextButton(onClick = { onAction(secondary.action, null) }) {
                    Text(secondary.label.resolve(state))
                }
            }
        }
    }
}

@Composable
private fun MarkActions(
    component: ActionsComponent,
    state: MarkUiState,
    onAction: (BduiAction, String?) -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            component.items.forEach { item ->
                TextButton(
                    onClick = {
                        val runtimeValue = if (item.action.operation == dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiOperation.SELECT_DATE) {
                            "today"
                        } else {
                            null
                        }
                        onAction(item.action, runtimeValue)
                    },
                ) {
                    Text(item.label.resolve(state))
                }
            }
        }
    }
}

private fun String.resolve(state: MarkUiState): String {
    return this
        .replace("{selectedDate}", state.selectedDate.format(MarkViewModel.UiDateFormatter))
        .replace("{templateSource}", state.templateSource)
}
