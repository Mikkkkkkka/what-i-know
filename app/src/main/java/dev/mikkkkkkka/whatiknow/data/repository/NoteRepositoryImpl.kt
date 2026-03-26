package dev.mikkkkkkka.whatiknow.data.repository

import dev.mikkkkkkka.whatiknow.domain.model.Note
import dev.mikkkkkkka.whatiknow.domain.model.NoteSummary
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl : NoteRepository {
    override fun getNoteSummaries(): Flow<List<NoteSummary>> {
        TODO("Not yet implemented")
    }

    override suspend fun getNoteIds(): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getNoteNames(): List<String> {
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

