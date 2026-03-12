package dev.mikkkkkkka.whatiknow.data.repository

import dev.mikkkkkkka.whatiknow.domain.model.Note
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository

class NoteRepositoryImpl : NoteRepository {
    override suspend fun getNoteIds(): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getNote(id: String): Note? {
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

