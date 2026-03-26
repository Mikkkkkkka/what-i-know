package dev.mikkkkkkka.whatiknow.domain.repository

import dev.mikkkkkkka.whatiknow.domain.model.Note
import dev.mikkkkkkka.whatiknow.domain.model.NoteSummary
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNoteSummaries(): Flow<List<NoteSummary>>

    suspend fun getNoteIds(): List<String>

    suspend fun getNoteNames(): List<String>

    suspend fun getNote(id: String): Note?

    suspend fun saveNote(note: Note)

    suspend fun deleteNote(id: String)

    suspend fun sync()
}
