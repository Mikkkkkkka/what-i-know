package com.example.whatiknow.di

import com.example.whatiknow.domain.usecase.DeleteNoteUseCase
import com.example.whatiknow.domain.usecase.GetNoteIdsUseCase
import com.example.whatiknow.domain.usecase.GetNoteUseCase
import com.example.whatiknow.domain.usecase.SaveNoteUseCase
import com.example.whatiknow.domain.usecase.SyncNoteUseCase

interface DomainModule {

    val getNoteIdsUseCase: GetNoteIdsUseCase

    val getNoteUseCase: GetNoteUseCase

    val saveNoteUseCase: SaveNoteUseCase

    val deleteNoteUseCase: DeleteNoteUseCase

    val syncNoteUseCase: SyncNoteUseCase

}
