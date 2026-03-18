package dev.mikkkkkkka.whatiknow.di.impl

import dev.mikkkkkkka.whatiknow.WhatIKnowApplication
import dev.mikkkkkkka.whatiknow.data.repository.LocalMarkRepository
import dev.mikkkkkkka.whatiknow.data.repository.LocalNoteRepository
import dev.mikkkkkkka.whatiknow.di.RepositoryModule
import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository

class RepositoryModuleImpl : RepositoryModule {

    override val noteRepository: NoteRepository by lazy {
        LocalNoteRepository.create(WhatIKnowApplication.appModule)
    }

    override val markRepository: MarkRepository by lazy {
        LocalMarkRepository.create(WhatIKnowApplication.appModule)
    }
}

