package dev.mikkkkkkka.whatiknow.domain.usecase.note

import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository
import javax.inject.Inject

class GetNoteNamesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(): List<String> {
        return repository.getNoteNames()
    }
}

