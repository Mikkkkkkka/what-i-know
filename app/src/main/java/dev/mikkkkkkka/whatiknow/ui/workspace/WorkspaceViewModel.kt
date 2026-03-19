package dev.mikkkkkkka.whatiknow.ui.workspace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.mikkkkkkka.whatiknow.di.AppModule
import dev.mikkkkkkka.whatiknow.domain.usecase.note.GetNoteIdsUseCase
import kotlinx.coroutines.launch

class WorkspaceViewModel(
    private val getNoteIds: GetNoteIdsUseCase,
) : ViewModel() {

    private val innerNotes = MutableLiveData<List<String>>()
    val notes: LiveData<List<String>> = innerNotes

    fun loadNotes() {
        viewModelScope.launch {
            innerNotes.value = getNoteIds()
        }
    }

    companion object {
        fun factory(appModule: AppModule): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WorkspaceViewModel(getNoteIds = appModule.getNoteIdsUseCase) as T
                }
            }
        }
    }
}
