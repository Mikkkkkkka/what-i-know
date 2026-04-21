package dev.mikkkkkkka.whatiknow.data.remote

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal fun LocalDateTime.toBackendDateTime(): String =
    atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

internal fun parseBackendTimestamp(value: String): LocalDateTime =
    OffsetDateTime.parse(value).withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime()
