package dev.mikkkkkkka.whatiknow.di

import dev.mikkkkkkka.whatiknow.domain.usecase.note.DeleteNoteUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.note.GetNoteIdsUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.note.GetNoteUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.note.SaveNoteUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.note.SyncNoteUseCase

interface DomainModule {

    val getNoteIdsUseCase: GetNoteIdsUseCase

    val getNoteUseCase: GetNoteUseCase

    val saveNoteUseCase: SaveNoteUseCase

    val deleteNoteUseCase: DeleteNoteUseCase

    val syncNoteUseCase: SyncNoteUseCase

}

