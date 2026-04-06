package dev.mikkkkkkka.whatiknow.data.local

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.mikkkkkkka.whatiknow.data.local.dao.PendingDeletionDao
import dev.mikkkkkkka.whatiknow.data.local.dao.MarkDao
import dev.mikkkkkkka.whatiknow.data.local.dao.NoteDao
import dev.mikkkkkkka.whatiknow.data.local.entities.MarkEntity
import dev.mikkkkkkka.whatiknow.data.local.entities.NoteEntity
import dev.mikkkkkkka.whatiknow.data.local.entities.PendingDeletionEntity

@Database(entities = [NoteEntity::class, MarkEntity::class, PendingDeletionEntity::class], version = 2)
@TypeConverters(RoomTypeConverters::class)
abstract class NoteMarkDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    abstract fun markDao(): MarkDao

    abstract fun pendingDeletionDao(): PendingDeletionDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `PendingDeletionEntity` (
                        `itemType` TEXT NOT NULL,
                        `itemId` TEXT NOT NULL,
                        `deletedAt` TEXT NOT NULL,
                        PRIMARY KEY(`itemType`, `itemId`)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
