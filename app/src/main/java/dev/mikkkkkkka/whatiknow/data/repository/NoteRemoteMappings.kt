package dev.mikkkkkkka.whatiknow.data.repository

import dev.mikkkkkkka.whatiknow.data.local.entities.NoteEntity
import dev.mikkkkkkka.whatiknow.data.remote.dto.CreateNoteRequestDto
import dev.mikkkkkkka.whatiknow.data.remote.dto.NoteResponseDto
import dev.mikkkkkkka.whatiknow.data.remote.dto.UpdateNoteRequestDto
import dev.mikkkkkkka.whatiknow.data.remote.parseBackendTimestamp

internal fun NoteEntity.toCreateRequest(): CreateNoteRequestDto =
    CreateNoteRequestDto(
        id = id,
        title = name,
        content = content,
    )

internal fun NoteEntity.toUpdateRequest(): UpdateNoteRequestDto =
    UpdateNoteRequestDto(
        title = name,
        content = content,
    )

internal fun NoteResponseDto.toEntity(): NoteEntity =
    NoteEntity(
        id = id,
        name = title,
        content = content,
        updatedAt = parseBackendTimestamp(updated_at),
    )
