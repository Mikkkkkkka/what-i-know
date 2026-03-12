package com.example.whatiknow.di.impl

import com.example.whatiknow.WhatIKnowApplication
import com.example.whatiknow.di.DomainModule
import com.example.whatiknow.domain.usecase.DeleteNoteUseCase
import com.example.whatiknow.domain.usecase.GetNoteIdsUseCase
import com.example.whatiknow.domain.usecase.GetNoteUseCase
import com.example.whatiknow.domain.usecase.SaveNoteUseCase
import com.example.whatiknow.domain.usecase.SyncNoteUseCase

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
