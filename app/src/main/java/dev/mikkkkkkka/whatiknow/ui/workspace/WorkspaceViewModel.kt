package dev.mikkkkkkka.whatiknow.ui.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikkkkkkka.whatiknow.domain.usecase.note.GetNoteSummariesUseCase
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    getNoteSummaries: GetNoteSummariesUseCase,
) : ViewModel() {
    val notes = getNoteSummaries().asLiveData(viewModelScope.coroutineContext)
}
