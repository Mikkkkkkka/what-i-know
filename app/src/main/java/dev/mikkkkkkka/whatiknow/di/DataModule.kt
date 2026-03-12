package dev.mikkkkkkka.whatiknow.di

import dev.mikkkkkkka.whatiknow.data.local.NoteDao
import dev.mikkkkkkka.whatiknow.data.local.NoteDatabase
import dev.mikkkkkkka.whatiknow.data.mapper.RoomNoteEntityMapper

interface DataModule {

    val noteDatabase: NoteDatabase

    val noteDao: NoteDao

    val roomNoteEntityMapper: RoomNoteEntityMapper

}

