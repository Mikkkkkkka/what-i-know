package dev.mikkkkkkka.whatiknow.di.impl

import dev.mikkkkkkka.whatiknow.WhatIKnowApplication
import dev.mikkkkkkka.whatiknow.data.repository.LocalNoteRepository
import dev.mikkkkkkka.whatiknow.di.RepositoryModule
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository

class RepositoryModuleImpl : RepositoryModule {

    override val noteRepository: NoteRepository by lazy {
        LocalNoteRepository.create(WhatIKnowApplication.appModule)
    }

}

