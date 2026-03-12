package dev.mikkkkkkka.whatiknow.di

import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository

interface RepositoryModule {

    val noteRepository: NoteRepository

}

