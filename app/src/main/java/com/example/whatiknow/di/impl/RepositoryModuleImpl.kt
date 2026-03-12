package com.example.whatiknow.di.impl

import com.example.whatiknow.WhatIKnowApplication
import com.example.whatiknow.data.repository.LocalNoteRepository
import com.example.whatiknow.di.RepositoryModule
import com.example.whatiknow.domain.repository.NoteRepository

class RepositoryModuleImpl : RepositoryModule {

    override val noteRepository: NoteRepository by lazy {
        LocalNoteRepository.create(WhatIKnowApplication.appModule)
    }

}
