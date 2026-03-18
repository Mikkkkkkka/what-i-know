package dev.mikkkkkkka.whatiknow.domain.usecase.note

import dev.mikkkkkkka.whatiknow.di.AppModule
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository

class SyncNotesUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke() {
        return repository.sync()
    }

    companion object {
        fun create(appModule: AppModule): SyncNotesUseCase {
            return SyncNotesUseCase(appModule.noteRepository)
        }
    }
}

