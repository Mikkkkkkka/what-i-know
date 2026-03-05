package com.example.whatiknow.domain.model.notes

import java.time.LocalDate

data class DailyNote(
    val id: Int,
    val date: LocalDate,
    var content: String,
)
