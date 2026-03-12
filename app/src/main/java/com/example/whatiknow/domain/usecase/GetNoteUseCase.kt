package com.example.whatiknow.domain.usecase

import com.example.whatiknow.di.AppModule
import com.example.whatiknow.domain.model.Note
import com.example.whatiknow.domain.repository.NoteRepository

class GetNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: String): Note? {
        return repository.getNote(id)
    }

    companion object {
        fun create(appModule: AppModule): GetNoteUseCase {
            return GetNoteUseCase(appModule.noteRepository)
        }
    }
}
