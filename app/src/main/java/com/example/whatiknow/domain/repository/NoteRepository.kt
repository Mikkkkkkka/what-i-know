package com.example.whatiknow.domain.repository

import com.example.whatiknow.domain.model.Note

interface NoteRepository {
    suspend fun getNoteIds(): List<String>

    suspend fun getNote(): Note

    suspend fun saveNote(note: Note)

    suspend fun deleteNote(id: String)

    suspend fun sync()
}