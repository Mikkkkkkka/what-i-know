package dev.mikkkkkkka.whatiknow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NoteEntity::class, MarkEntity::class], version = 1)
abstract class NoteMarkDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    abstract fun markDao(): MarkDao
}
