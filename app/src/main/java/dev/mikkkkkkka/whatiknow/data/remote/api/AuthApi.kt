package dev.mikkkkkkka.whatiknow.data.remote.api

import dev.mikkkkkkka.whatiknow.data.remote.dto.LoginRequestDto
import dev.mikkkkkkka.whatiknow.data.remote.dto.LoginResponseDto
import dev.mikkkkkkka.whatiknow.data.remote.dto.RegisterRequestDto
import dev.mikkkkkkka.whatiknow.data.remote.dto.StatusResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto,
    ): Response<LoginResponseDto>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto,
    ): Response<StatusResponseDto>
}
