package dev.mikkkkkkka.whatiknow.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.mikkkkkkka.whatiknow.data.local.NoteMarkDatabase
import dev.mikkkkkkka.whatiknow.data.local.dao.MarkDao
import dev.mikkkkkkka.whatiknow.data.local.dao.NoteDao
import dev.mikkkkkkka.whatiknow.data.mapper.RoomMarkEntityMapper
import dev.mikkkkkkka.whatiknow.data.mapper.RoomNoteEntityMapper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideNoteMarkDatabase(
        @ApplicationContext context: Context,
    ): NoteMarkDatabase {
        return Room.databaseBuilder(
            context,
            NoteMarkDatabase::class.java,
            "note.db"
        ).build()
    }

    @Provides
    fun provideNoteDao(
        database: NoteMarkDatabase,
    ): NoteDao = database.noteDao()

    @Provides
    fun provideMarkDao(
        database: NoteMarkDatabase,
    ): MarkDao = database.markDao()

    @Provides
    fun provideRoomNoteEntityMapper(): RoomNoteEntityMapper = RoomNoteEntityMapper()

    @Provides
    fun provideRoomMarkEntityMapper(): RoomMarkEntityMapper = RoomMarkEntityMapper()
}

