package dev.mikkkkkkka.whatiknow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class NoteEntity(
    @PrimaryKey val id: String,
    val content: String,
    val updatedAt: LocalDateTime,
)

