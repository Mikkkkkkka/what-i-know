package dev.mikkkkkkka.whatiknow.domain.usecase

import dev.mikkkkkkka.whatiknow.di.AppModule
import dev.mikkkkkkka.whatiknow.domain.model.Note
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository

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

