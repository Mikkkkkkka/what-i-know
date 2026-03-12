package dev.mikkkkkkka.whatiknow.domain.usecase

import dev.mikkkkkkka.whatiknow.di.AppModule
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository

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

