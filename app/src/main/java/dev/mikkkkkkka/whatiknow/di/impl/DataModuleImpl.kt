package dev.mikkkkkkka.whatiknow.di.impl

import android.content.Context
import androidx.room.Room
import dev.mikkkkkkka.whatiknow.data.local.dao.NoteDao
import dev.mikkkkkkka.whatiknow.data.local.NoteMarkDatabase
import dev.mikkkkkkka.whatiknow.data.mapper.RoomNoteEntityMapper
import dev.mikkkkkkka.whatiknow.di.DataModule

class DataModuleImpl(
    private val context: Context
) : DataModule {

    override val noteMarkDatabase: NoteMarkDatabase by lazy {
        Room.databaseBuilder(
            context,
            NoteMarkDatabase::class.java,
            "note.db"
        ).build()
    }

    override val noteDao: NoteDao by lazy {
        noteMarkDatabase.noteDao()
    }

    override val roomNoteEntityMapper: RoomNoteEntityMapper by lazy {
        RoomNoteEntityMapper()
    }

}

