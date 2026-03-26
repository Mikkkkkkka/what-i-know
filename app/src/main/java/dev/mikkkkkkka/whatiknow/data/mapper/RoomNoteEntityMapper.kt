package dev.mikkkkkkka.whatiknow.data.mapper

import dev.mikkkkkkka.whatiknow.data.local.entities.NoteEntity
import dev.mikkkkkkka.whatiknow.domain.model.Note
import java.time.LocalDateTime

class RoomNoteEntityMapper {
    fun map(roomNote: NoteEntity): Note {
        return Note(
            roomNote.id,
            roomNote.name,
            roomNote.content,
        )
    }

    fun unmap(note: Note, updatedAt: LocalDateTime): NoteEntity {
        return NoteEntity(
            note.id,
            note.name,
            note.content,
            updatedAt,
        )
    }
}
