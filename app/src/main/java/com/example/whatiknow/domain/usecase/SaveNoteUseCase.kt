package com.example.whatiknow.domain.usecase

import com.example.whatiknow.di.AppModule
import com.example.whatiknow.domain.model.Note
import com.example.whatiknow.domain.repository.NoteRepository

class SaveNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.saveNote(note)
    }

    companion object {
        fun create(appModule: AppModule): SaveNoteUseCase {
            return SaveNoteUseCase(appModule.noteRepository)
        }
    }
}
