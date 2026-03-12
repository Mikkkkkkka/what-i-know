package com.example.whatiknow.domain.usecase

import com.example.whatiknow.di.AppModule
import com.example.whatiknow.domain.repository.NoteRepository

class SyncNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke() {
        return repository.sync()
    }

    companion object {
        fun create(appModule: AppModule): SyncNoteUseCase {
            return SyncNoteUseCase(appModule.noteRepository)
        }
    }
}
