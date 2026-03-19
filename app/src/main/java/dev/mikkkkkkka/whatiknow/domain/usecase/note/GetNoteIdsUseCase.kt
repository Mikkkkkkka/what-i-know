package dev.mikkkkkkka.whatiknow.domain.usecase.note

import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository
import javax.inject.Inject

class GetNoteIdsUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(): List<String> {
        return repository.getNoteIds()
    }
}

