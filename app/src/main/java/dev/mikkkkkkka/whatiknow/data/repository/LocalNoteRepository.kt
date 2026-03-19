package dev.mikkkkkkka.whatiknow.data.repository

import dev.mikkkkkkka.whatiknow.data.local.dao.NoteDao
import dev.mikkkkkkka.whatiknow.data.mapper.RoomNoteEntityMapper
import dev.mikkkkkkka.whatiknow.domain.model.Note
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository
import java.time.LocalDateTime
import javax.inject.Inject

class LocalNoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val mapper: RoomNoteEntityMapper,
) : NoteRepository {

    override suspend fun getNoteIds(): List<String> {
        return noteDao.getAllIds()
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

