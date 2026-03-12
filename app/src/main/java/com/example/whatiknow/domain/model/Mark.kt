package com.example.whatiknow.domain.model

import java.time.LocalDate

data class Mark(
    val id: Int,
    val date: LocalDate,
    var content: String,
)