package dev.mikkkkkkka.whatiknow.data.remote.dto

data class CreateMarkRequestDto(
    val id: String,
    val date: String,
    val content: String,
)

data class UpdateMarkRequestDto(
    val content: String,
)

data class MarkResponseDto(
    val id: String,
    val date: String,
    val content: String,
    val updated_at: String,
)
