package dev.mikkkkkkka.whatiknow.data.repository

import dev.mikkkkkkka.whatiknow.data.local.dao.NoteDao
import dev.mikkkkkkka.whatiknow.data.mapper.RoomNoteEntityMapper
import dev.mikkkkkkka.whatiknow.domain.model.Note
import dev.mikkkkkkka.whatiknow.domain.model.NoteSummary
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class LocalNoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val mapper: RoomNoteEntityMapper,
) : NoteRepository {

    override fun getNoteSummaries(): Flow<List<NoteSummary>> {
        return noteDao.observeAll().map { notes ->
            notes.map { note ->
                NoteSummary(
                    id = note.id,
                    name = note.name,
                )
            }
        }
    }

    override suspend fun getNoteIds(): List<String> {
        return noteDao.getAllIds()
    }

    override suspend fun getNoteNames(): List<String> {
        return noteDao.getAllNames()
    }

    override suspend fun getNote(id: String): Note? {
        val noteEntity = noteDao.getById(id)
        return noteEntity?.let { mapper.map(it) }
    }

    override suspend fun saveNote(note: Note) {
        val updatedAt = LocalDateTime.now()
        val noteEntity = mapper.unmap(note, updatedAt)
        when (noteDao.getById(note.id)) {
            null -> noteDao.insert(noteEntity)
            else -> noteDao.update(noteEntity)
        }
    }

    override suspend fun deleteNote(id: String) {
        noteDao.delete(id)
    }

    override suspend fun sync() {
        // no external sync happening
    }
}

