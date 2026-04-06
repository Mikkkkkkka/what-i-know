package dev.mikkkkkkka.whatiknow.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mikkkkkkka.whatiknow.data.repository.AuthRepositoryImpl
import dev.mikkkkkkka.whatiknow.data.repository.SyncedMarkRepository
import dev.mikkkkkkka.whatiknow.data.repository.SyncedNoteRepository
import dev.mikkkkkkka.whatiknow.domain.repository.AuthRepository
import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        repository: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        repository: SyncedNoteRepository,
    ): NoteRepository

    @Binds
    @Singleton
    abstract fun bindMarkRepository(
        repository: SyncedMarkRepository,
    ): MarkRepository
}

