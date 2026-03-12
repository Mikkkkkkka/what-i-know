package dev.mikkkkkkka.whatiknow.di

import dev.mikkkkkkka.whatiknow.domain.usecase.DeleteNoteUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.GetNoteIdsUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.GetNoteUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.SaveNoteUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.SyncNoteUseCase

interface DomainModule {

    val getNoteIdsUseCase: GetNoteIdsUseCase

    val getNoteUseCase: GetNoteUseCase

    val saveNoteUseCase: SaveNoteUseCase

    val deleteNoteUseCase: DeleteNoteUseCase

    val syncNoteUseCase: SyncNoteUseCase

}

