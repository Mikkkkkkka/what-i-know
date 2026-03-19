package dev.mikkkkkkka.whatiknow.ui.workspace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikkkkkkka.whatiknow.domain.usecase.note.GetNoteIdsUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val getNoteIds: GetNoteIdsUseCase,
) : ViewModel() {

    private val innerNotes = MutableLiveData<List<String>>()
    val notes: LiveData<List<String>> = innerNotes

    fun loadNotes() {
        viewModelScope.launch {
            innerNotes.value = getNoteIds()
        }
    }
}
