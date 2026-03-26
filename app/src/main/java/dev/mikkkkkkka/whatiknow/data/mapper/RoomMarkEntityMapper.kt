package dev.mikkkkkkka.whatiknow.data.mapper

import dev.mikkkkkkka.whatiknow.data.local.entities.MarkEntity
import dev.mikkkkkkka.whatiknow.domain.model.Mark
import java.time.LocalDateTime

class RoomMarkEntityMapper {
    fun map(roomMark: MarkEntity): Mark {
        return Mark(
            roomMark.id,
            roomMark.date,
            roomMark.content,
        )
    }

    fun unmap(note: Mark, updatedAt: LocalDateTime): MarkEntity {
        return MarkEntity(
            note.id,
            note.date,
            note.content,
            updatedAt,
        )
    }
}
