package dev.mikkkkkkka.whatiknow.di

import dev.mikkkkkkka.whatiknow.data.local.dao.NoteDao
import dev.mikkkkkkka.whatiknow.data.local.NoteMarkDatabase
import dev.mikkkkkkka.whatiknow.data.mapper.RoomNoteEntityMapper

interface DataModule {

    val noteMarkDatabase: NoteMarkDatabase

    val noteDao: NoteDao

    val roomNoteEntityMapper: RoomNoteEntityMapper

}

