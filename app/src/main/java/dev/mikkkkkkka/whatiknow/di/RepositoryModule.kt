package dev.mikkkkkkka.whatiknow.di

import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository

interface RepositoryModule {

    val noteRepository: NoteRepository

    val markRepository: MarkRepository
}

