package dev.mikkkkkkka.whatiknow.domain.model

import java.time.LocalDate

data class Mark(
    val id: String,
    val date: LocalDate,
    var content: String,
)
