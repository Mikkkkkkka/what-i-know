package com.example.whatiknow.domain.usecase

import com.example.whatiknow.domain.model.Note
import com.example.whatiknow.domain.repository.NoteRepository

class GetNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(): List<Note> {
        return repository.getNotes()
    }
}