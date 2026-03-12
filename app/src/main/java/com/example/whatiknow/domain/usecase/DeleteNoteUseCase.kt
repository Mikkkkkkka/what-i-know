package com.example.whatiknow.domain.usecase

import com.example.whatiknow.domain.repository.NoteRepository

class DeleteNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteNote(id)
    }
}