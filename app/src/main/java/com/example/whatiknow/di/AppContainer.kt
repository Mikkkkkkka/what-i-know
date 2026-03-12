package com.example.whatiknow.di

import android.content.Context
import androidx.room.Room
import com.example.whatiknow.data.local.NoteDatabase
import com.example.whatiknow.data.mapper.RoomNoteEntityMapper
import com.example.whatiknow.data.repository.LocalNoteRepository

class AppContainer(context: Context) {

    val localDatabase: NoteDatabase by lazy {
        Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            "noted.db"
        ).build()
    }

    val noteDao by lazy {
        localDatabase.noteDao()
    }

    val roomNoteEntityMapper by lazy {
        RoomNoteEntityMapper()
    }

    val noteRepository by lazy {
        LocalNoteRepository(noteDao, roomNoteEntityMapper)
    }
}