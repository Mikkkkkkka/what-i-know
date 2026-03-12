package dev.mikkkkkkka.whatiknow.domain.usecase

import dev.mikkkkkkka.whatiknow.di.AppModule
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository

class DeleteNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteNote(id)
    }

    companion object {
        fun create(appModule: AppModule): DeleteNoteUseCase {
            return DeleteNoteUseCase(appModule.noteRepository)
        }
    }
}

