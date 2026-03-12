package com.example.whatiknow.di

import com.example.whatiknow.domain.repository.NoteRepository

interface RepositoryModule {

    val noteRepository: NoteRepository

}
