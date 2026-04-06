package dev.mikkkkkkka.whatiknow.data.repository

import java.time.LocalDateTime
import java.time.ZoneOffset

internal fun nowUtc(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
