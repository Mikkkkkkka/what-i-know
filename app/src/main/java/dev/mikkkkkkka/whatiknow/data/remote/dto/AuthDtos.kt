package dev.mikkkkkkka.whatiknow.data.remote.dto

data class LoginRequestDto(
    val username: String,
    val password: String,
)

data class RegisterRequestDto(
    val username: String,
    val password: String,
)

data class LoginResponseDto(
    val token: String,
)

data class StatusResponseDto(
    val status: String,
)

data class ErrorResponseDto(
    val error: String,
)
