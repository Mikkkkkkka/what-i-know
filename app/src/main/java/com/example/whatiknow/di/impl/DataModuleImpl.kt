package com.example.whatiknow.di.impl

import android.content.Context
import androidx.room.Room
import com.example.whatiknow.data.local.NoteDao
import com.example.whatiknow.data.local.NoteDatabase
import com.example.whatiknow.data.mapper.RoomNoteEntityMapper
import com.example.whatiknow.di.DataModule

class DataModuleImpl(
    private val context: Context
) : DataModule {

    override val noteDatabase: NoteDatabase by lazy {
        Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            "note.db"
        ).build()
    }

    override val noteDao: NoteDao by lazy {
        noteDatabase.noteDao()
    }

    override val roomNoteEntityMapper: RoomNoteEntityMapper by lazy {
        RoomNoteEntityMapper()
    }

}
