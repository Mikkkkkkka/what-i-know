package dev.mikkkkkkka.whatiknow.ui.mark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikkkkkkka.whatiknow.data.remote.BduiRepository
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiAction
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiActionKind
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiDestination
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiOperation
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiScreen
import dev.mikkkkkkka.whatiknow.data.remote.bdui.EmbeddedBduiTemplates
import dev.mikkkkkkka.whatiknow.domain.model.Mark
import dev.mikkkkkkka.whatiknow.domain.usecase.mark.DeleteMarkUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.mark.GetMarkUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.mark.GetMarksUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.mark.SaveMarkUseCase
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MarkUiState(
    val isLoading: Boolean = true,
    val screen: BduiScreen? = null,
    val templateSource: String = "booting",
    val selectedDate: LocalDate = LocalDate.now(),
    val markId: String? = null,
    val content: String = "",
    val heatmapValues: Map<LocalDate, Int> = emptyMap(),
    val message: String? = null,
    val navigateToWorkspace: Boolean = false,
)

@HiltViewModel
class MarkViewModel @Inject constructor(
    private val repository: BduiRepository,
    private val gson: Gson,
    private val getMarkUseCase: GetMarkUseCase,
    private val getMarksUseCase: GetMarksUseCase,
    private val saveMarkUseCase: SaveMarkUseCase,
    private val deleteMarkUseCase: DeleteMarkUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MarkUiState())
    val state: StateFlow<MarkUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, message = null) }
            repository.seedScreenIfMissing("mark", EmbeddedBduiTemplates.mark(gson))
            val screenSnapshot = repository.loadScreen("mark", EmbeddedBduiTemplates.mark(gson))
            val nextState = loadDate(
                selectedDate = _state.value.selectedDate,
                screen = screenSnapshot.screen,
                source = screenSnapshot.source,
            )
            _state.value = nextState.copy(isLoading = false)
        }
    }

    fun onBodyChange(value: String) {
        _state.update { it.copy(content = value, message = null) }
    }

    fun onAction(action: BduiAction, runtimeValue: String? = null) {
        when (action.kind) {
            BduiActionKind.NAVIGATE -> {
                if (action.destination == BduiDestination.WORKSPACE) {
                    _state.update { it.copy(navigateToWorkspace = true) }
                }
            }

            BduiActionKind.COMMAND -> when (action.operation) {
                BduiOperation.SELECT_DATE -> {
                    val targetDate = runtimeValue?.let(::parseRuntimeDate) ?: LocalDate.now()
                    selectDate(targetDate)
                }

                BduiOperation.SAVE_MARK -> saveMark()
                BduiOperation.DELETE_MARK -> deleteMark()
                BduiOperation.REFRESH -> refresh()
                else -> Unit
            }
        }
    }

    fun onNavigationHandled() {
        _state.update { it.copy(navigateToWorkspace = false) }
    }

    private fun selectDate(date: LocalDate) {
        viewModelScope.launch {
            val current = _state.value
            persistCurrentState(current.content, current.selectedDate)
            val loaded = loadDate(
                selectedDate = date,
                screen = current.screen,
                source = current.templateSource,
            )
            _state.value = loaded.copy(isLoading = false)
        }
    }

    private fun saveMark() {
        viewModelScope.launch {
            val current = _state.value
            persistCurrentState(current.content, current.selectedDate)
            val reloaded = loadDate(
                selectedDate = current.selectedDate,
                screen = current.screen,
                source = current.templateSource,
                message = if (current.content.trim().isBlank()) "Day cleared" else "Day saved",
            )
            _state.value = reloaded.copy(isLoading = false)
        }
    }

    private fun deleteMark() {
        viewModelScope.launch {
            val current = _state.value
            deleteMarkUseCase(current.selectedDate)
            val reloaded = loadDate(
                selectedDate = current.selectedDate,
                screen = current.screen,
                source = current.templateSource,
                message = "Day cleared",
            )
            _state.value = reloaded.copy(isLoading = false)
        }
    }

    private suspend fun loadDate(
        selectedDate: LocalDate,
        screen: BduiScreen?,
        source: String,
        message: String? = null,
    ): MarkUiState {
        val month = YearMonth.from(selectedDate)
        val loadedMark = getMarkUseCase(selectedDate)
        val heatmapValues = getMarksUseCase(month.atDay(1), month.plusMonths(1).atDay(1))
            .groupingBy { it.date }
            .eachCount()

        return MarkUiState(
            isLoading = false,
            screen = screen,
            templateSource = source,
            selectedDate = selectedDate,
            markId = loadedMark?.id,
            content = loadedMark?.content.orEmpty(),
            heatmapValues = heatmapValues,
            message = message,
        )
    }

    private suspend fun persistCurrentState(content: String, date: LocalDate) {
        val normalizedContent = content.trimEnd()
        if (normalizedContent.isBlank()) {
            deleteMarkUseCase(date)
            return
        }

        saveMarkUseCase(
            Mark(
                id = _state.value.markId ?: UUID.randomUUID().toString(),
                date = date,
                content = normalizedContent,
            )
        )
    }

    private fun parseRuntimeDate(value: String): LocalDate {
        return if (value == "today") {
            LocalDate.now()
        } else {
            LocalDate.parse(value)
        }
    }

    companion object {
        val UiDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    }
}
