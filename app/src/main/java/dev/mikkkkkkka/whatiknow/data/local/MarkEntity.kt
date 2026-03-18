package dev.mikkkkkkka.whatiknow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
data class MarkEntity(
    @PrimaryKey val date: LocalDate,
    val content: String,
    val updatedAt: LocalDateTime,
)

