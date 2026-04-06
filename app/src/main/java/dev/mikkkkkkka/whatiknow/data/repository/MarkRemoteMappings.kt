package dev.mikkkkkkka.whatiknow.data.repository

import dev.mikkkkkkka.whatiknow.data.local.entities.MarkEntity
import dev.mikkkkkkka.whatiknow.data.remote.dto.CreateMarkRequestDto
import dev.mikkkkkkka.whatiknow.data.remote.dto.MarkResponseDto
import dev.mikkkkkkka.whatiknow.data.remote.dto.UpdateMarkRequestDto
import dev.mikkkkkkka.whatiknow.data.remote.parseBackendTimestamp
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal fun MarkEntity.toCreateRequest(): CreateMarkRequestDto =
    CreateMarkRequestDto(
        id = id,
        date = date.atStartOfDay().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        content = content,
    )

internal fun MarkEntity.toUpdateRequest(): UpdateMarkRequestDto =
    UpdateMarkRequestDto(
        content = content,
    )

internal fun MarkResponseDto.toEntity(): MarkEntity =
    MarkEntity(
        id = id,
        date = OffsetDateTime.parse(date).withOffsetSameInstant(ZoneOffset.UTC).toLocalDate(),
        content = content,
        updatedAt = parseBackendTimestamp(updated_at),
    )
