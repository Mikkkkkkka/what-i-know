package dev.mikkkkkkka.whatiknow.di.impl

import dev.mikkkkkkka.whatiknow.WhatIKnowApplication
import dev.mikkkkkkka.whatiknow.di.DomainModule
import dev.mikkkkkkka.whatiknow.domain.usecase.note.DeleteNoteUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.note.GetNoteIdsUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.note.GetNoteUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.note.SaveNoteUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.note.SyncNotesUseCase

class DomainModuleImpl : DomainModule {

    override val getNoteIdsUseCase: GetNoteIdsUseCase by lazy {
        GetNoteIdsUseCase.create(WhatIKnowApplication.appModule)
    }

    override val getNoteUseCase: GetNoteUseCase by lazy {
        GetNoteUseCase.create(WhatIKnowApplication.appModule)
    }

    override val saveNoteUseCase: SaveNoteUseCase by lazy {
        SaveNoteUseCase.create(WhatIKnowApplication.appModule)
    }

    override val deleteNoteUseCase: DeleteNoteUseCase by lazy {
        DeleteNoteUseCase.create(WhatIKnowApplication.appModule)
    }

    override val syncNoteUseCase: SyncNotesUseCase by lazy {
        SyncNotesUseCase.create(WhatIKnowApplication.appModule)
    }

}

