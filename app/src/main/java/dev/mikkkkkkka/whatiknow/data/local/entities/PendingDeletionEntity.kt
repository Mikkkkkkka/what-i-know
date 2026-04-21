package dev.mikkkkkkka.whatiknow.data.local.entities

import androidx.room.Entity
import java.time.LocalDateTime

@Entity(primaryKeys = ["itemType", "itemId"])
data class PendingDeletionEntity(
    val itemType: String,
    val itemId: String,
    val deletedAt: LocalDateTime,
)
