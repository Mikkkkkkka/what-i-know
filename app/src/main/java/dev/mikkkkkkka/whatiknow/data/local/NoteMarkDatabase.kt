package dev.mikkkkkkka.whatiknow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.mikkkkkkka.whatiknow.data.local.dao.MarkDao
import dev.mikkkkkkka.whatiknow.data.local.dao.NoteDao
import dev.mikkkkkkka.whatiknow.data.local.entities.MarkEntity
import dev.mikkkkkkka.whatiknow.data.local.entities.NoteEntity

@Database(entities = [NoteEntity::class, MarkEntity::class], version = 1)
@TypeConverters(RoomTypeConverters::class)
abstract class NoteMarkDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    abstract fun markDao(): MarkDao
}
