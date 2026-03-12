package com.example.whatiknow.domain.usecase

import com.example.whatiknow.domain.model.Note
import com.example.whatiknow.domain.repository.NoteRepository

class SaveNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.saveNote(note)
    }
}