package dev.mikkkkkkka.whatiknow.domain.usecase

import dev.mikkkkkkka.whatiknow.di.AppModule
import dev.mikkkkkkka.whatiknow.domain.model.Note
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository

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

