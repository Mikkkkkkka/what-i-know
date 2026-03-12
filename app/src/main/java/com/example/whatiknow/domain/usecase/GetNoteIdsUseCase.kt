package com.example.whatiknow.domain.usecase

import com.example.whatiknow.di.AppModule
import com.example.whatiknow.domain.repository.NoteRepository

class GetNoteIdsUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(): List<String> {
        return repository.getNoteIds()
    }

    companion object {
        fun create(appModule: AppModule): GetNoteIdsUseCase {
            return GetNoteIdsUseCase(appModule.noteRepository)
        }
    }
}
