package com.example.whatiknow.di

import com.example.whatiknow.data.local.NoteDao
import com.example.whatiknow.data.local.NoteDatabase
import com.example.whatiknow.data.mapper.RoomNoteEntityMapper

interface DataModule {

    val noteDatabase: NoteDatabase

    val noteDao: NoteDao

    val roomNoteEntityMapper: RoomNoteEntityMapper

}
