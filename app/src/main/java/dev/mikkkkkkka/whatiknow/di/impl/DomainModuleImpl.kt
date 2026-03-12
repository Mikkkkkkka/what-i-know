package dev.mikkkkkkka.whatiknow.di.impl

import dev.mikkkkkkka.whatiknow.WhatIKnowApplication
import dev.mikkkkkkka.whatiknow.di.DomainModule
import dev.mikkkkkkka.whatiknow.domain.usecase.DeleteNoteUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.GetNoteIdsUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.GetNoteUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.SaveNoteUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.SyncNoteUseCase

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

    override val syncNoteUseCase: SyncNoteUseCase by lazy {
        SyncNoteUseCase.create(WhatIKnowApplication.appModule)
    }

}

