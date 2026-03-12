package com.example.whatiknow.data.mapper

import com.example.whatiknow.data.local.NoteEntity
import com.example.whatiknow.domain.model.Note
import java.time.LocalDateTime

class RoomNoteEntityMapper {
    fun map(roomNote: NoteEntity): Note {
        return Note(
            roomNote.id,
            roomNote.content,
        )
    }

    fun unmap(note: Note, updatedAt: LocalDateTime): NoteEntity {
        return NoteEntity(
            note.id,
            note.content,
            updatedAt,
        )
    }
}