package dev.mikkkkkkka.whatiknow.domain.usecase

import dev.mikkkkkkka.whatiknow.di.AppModule
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository

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

