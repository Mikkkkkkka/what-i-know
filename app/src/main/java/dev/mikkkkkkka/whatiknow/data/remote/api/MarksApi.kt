package dev.mikkkkkkka.whatiknow.data.remote.api

import dev.mikkkkkkka.whatiknow.data.remote.dto.CreateMarkRequestDto
import dev.mikkkkkkka.whatiknow.data.remote.dto.MarkResponseDto
import dev.mikkkkkkka.whatiknow.data.remote.dto.StatusResponseDto
import dev.mikkkkkkka.whatiknow.data.remote.dto.UpdateMarkRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface MarksApi {
    @GET("users/{userId}/marks")
    suspend fun listMarks(
        @Path("userId") userId: String,
    ): List<MarkResponseDto>

    @POST("marks/")
    suspend fun createMark(
        @Body request: CreateMarkRequestDto,
    ): Response<StatusResponseDto>

    @PATCH("marks/{markId}")
    suspend fun updateMark(
        @Path("markId") markId: String,
        @Body request: UpdateMarkRequestDto,
    ): Response<StatusResponseDto>

    @DELETE("marks/{markId}")
    suspend fun deleteMark(
        @Path("markId") markId: String,
    ): Response<Unit>
}
