package dev.mikkkkkkka.whatiknow.di.impl

import android.content.Context
import androidx.room.Room
import dev.mikkkkkkka.whatiknow.data.local.NoteDao
import dev.mikkkkkkka.whatiknow.data.local.NoteDatabase
import dev.mikkkkkkka.whatiknow.data.mapper.RoomNoteEntityMapper
import dev.mikkkkkkka.whatiknow.di.DataModule

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

