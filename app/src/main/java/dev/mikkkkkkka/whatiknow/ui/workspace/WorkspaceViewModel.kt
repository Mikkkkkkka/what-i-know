package dev.mikkkkkkka.whatiknow.ui.workspace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikkkkkkka.whatiknow.domain.usecase.note.GetNoteIdsUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.note.GetNoteNamesUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val getNoteIds: GetNoteIdsUseCase,
    private val getNoteNames: GetNoteNamesUseCase,
) : ViewModel() {

    private val innerNotes = MutableLiveData<List<Pair<String, String>>>()
    val notes: LiveData<List<Pair<String, String>>> = innerNotes

    fun loadNotes() {
        viewModelScope.launch {
            val ids = getNoteIds()
            val names = getNoteNames()

            innerNotes.value = ids.zip(names)
        }
    }
}
