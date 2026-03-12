package com.example.whatiknow.data.repository

import com.example.whatiknow.domain.model.Note
import com.example.whatiknow.domain.repository.NoteRepository

class NoteRepositoryImpl : NoteRepository {
    override suspend fun getNotes(): List<Note> {
        TODO("Not yet implemented")
    }

    override suspend fun saveNote(note: Note) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNote(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun sync() {
        TODO("Not yet implemented")
    }
}