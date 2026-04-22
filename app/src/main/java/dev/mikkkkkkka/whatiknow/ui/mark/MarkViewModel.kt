package dev.mikkkkkkka.whatiknow.ui.mark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikkkkkkka.whatiknow.domain.model.Mark
import dev.mikkkkkkka.whatiknow.domain.usecase.mark.DeleteMarkUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.mark.GetMarkUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.mark.GetMarksUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.mark.SaveMarkUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MarkViewModel @Inject constructor(
    private val getMarkUseCase: GetMarkUseCase,
    private val getMarksUseCase: GetMarksUseCase,
    private val saveMarkUseCase: SaveMarkUseCase,
    private val deleteMarkUseCase: DeleteMarkUseCase,
) : ViewModel() {

    private val innerState = MutableLiveData(MarkEditorState())
    val state: LiveData<MarkEditorState> = innerState

    private var autosaveJob: Job? = null

    fun loadInitialDate(date: LocalDate = LocalDate.now()) {
        val currentState = innerState.value ?: MarkEditorState()
        if (currentState.selectedDate == date && currentState.heatmapValues.isNotEmpty()) {
            return
        }

        viewModelScope.launch {
            loadDateIntoState(date)
        }
    }

    fun onContentChanged(content: String) {
        val currentState = innerState.value ?: MarkEditorState()
        innerState.value = currentState.copy(content = content)

        autosaveJob?.cancel()
        autosaveJob = viewModelScope.launch {
            delay(AUTOSAVE_DELAY_MS)
            persistCurrentState(content)
        }
    }

    fun selectDate(date: LocalDate) {
        viewModelScope.launch {
            autosaveJob?.cancel()
            persistCurrentState((innerState.value ?: MarkEditorState()).content)
            loadDateIntoState(date)
        }
    }

    fun saveImmediately(content: String) {
        autosaveJob?.cancel()
        viewModelScope.launch {
            persistCurrentState(content)
        }
    }

    private suspend fun loadDateIntoState(date: LocalDate) {
        val month = YearMonth.from(date)
        val loadedMark = getMarkUseCase(date)
        val heatmapValues = getMarksUseCase(month.atDay(1), month.plusMonths(1).atDay(1))
            .groupingBy { it.date }
            .eachCount()

        innerState.value = MarkEditorState(
            selectedDate = date,
            markId = loadedMark?.id,
            content = loadedMark?.content.orEmpty(),
            heatmapValues = heatmapValues,
        )
    }

    private suspend fun persistCurrentState(content: String) {
        val currentState = innerState.value ?: MarkEditorState()
        val normalizedContent = content.trimEnd()

        if (normalizedContent.isBlank()) {
            currentState.markId?.let {
                deleteMarkUseCase(currentState.selectedDate)
            }
            innerState.value = currentState.copy(
                markId = null,
                content = "",
                heatmapValues = currentState.heatmapValues - currentState.selectedDate,
            )
            return
        }

        val markId = currentState.markId ?: UUID.randomUUID().toString()
        saveMarkUseCase(
            Mark(
                id = markId,
                date = currentState.selectedDate,
                content = normalizedContent,
            )
        )

        innerState.value = currentState.copy(
            markId = markId,
            content = normalizedContent,
            heatmapValues = currentState.heatmapValues + (currentState.selectedDate to 1),
        )
    }

    companion object {
        private const val AUTOSAVE_DELAY_MS = 300L
    }
}

data class MarkEditorState(
    val selectedDate: LocalDate = LocalDate.now(),
    val markId: String? = null,
    val content: String = "",
    val heatmapValues: Map<LocalDate, Int> = emptyMap(),
)
