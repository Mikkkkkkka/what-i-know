package dev.mikkkkkkka.whatiknow.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mikkkkkkka.whatiknow.data.repository.LocalMarkRepository
import dev.mikkkkkkka.whatiknow.data.repository.LocalNoteRepository
import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        repository: LocalNoteRepository,
    ): NoteRepository

    @Binds
    @Singleton
    abstract fun bindMarkRepository(
        repository: LocalMarkRepository,
    ): MarkRepository
}

