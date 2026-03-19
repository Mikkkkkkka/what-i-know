package dev.mikkkkkkka.whatiknow.domain.usecase.note

import dev.mikkkkkkka.whatiknow.domain.model.Note
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.saveNote(note)
    }
}

