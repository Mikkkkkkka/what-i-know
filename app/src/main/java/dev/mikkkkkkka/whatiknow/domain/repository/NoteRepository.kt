package dev.mikkkkkkka.whatiknow.domain.repository

import dev.mikkkkkkka.whatiknow.domain.model.Note

interface NoteRepository {
    suspend fun getNoteIds(): List<String>

    suspend fun getNote(id: String): Note?

    suspend fun saveNote(note: Note)

    suspend fun deleteNote(id: String)

    suspend fun sync()
}
