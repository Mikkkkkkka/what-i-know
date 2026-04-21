package dev.mikkkkkkka.whatiknow.data.remote.dto

data class CreateNoteRequestDto(
    val id: String,
    val title: String,
    val content: String,
)

data class UpdateNoteRequestDto(
    val title: String,
    val content: String,
)

data class NoteResponseDto(
    val id: String,
    val user_id: String,
    val title: String,
    val content: String,
    val updated_at: String,
)
