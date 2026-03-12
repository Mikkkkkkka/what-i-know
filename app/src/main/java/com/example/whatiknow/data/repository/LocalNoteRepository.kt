package com.example.whatiknow.data.repository

import com.example.whatiknow.data.local.NoteDao
import com.example.whatiknow.data.mapper.RoomNoteEntityMapper
import com.example.whatiknow.domain.model.Note
import com.example.whatiknow.domain.repository.NoteRepository
import java.time.LocalDateTime

class LocalNoteRepository(
    var noteDao: NoteDao,
    var mapper: RoomNoteEntityMapper,
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