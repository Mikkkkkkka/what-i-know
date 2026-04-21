package dev.mikkkkkkka.whatiknow.data.remote.api

import dev.mikkkkkkka.whatiknow.data.remote.dto.CreateNoteRequestDto
import dev.mikkkkkkka.whatiknow.data.remote.dto.NoteResponseDto
import dev.mikkkkkkka.whatiknow.data.remote.dto.StatusResponseDto
import dev.mikkkkkkka.whatiknow.data.remote.dto.UpdateNoteRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface NotesApi {
    @GET("users/{userId}/notes")
    suspend fun listNotes(
        @Path("userId") userId: String,
    ): List<NoteResponseDto>

    @POST("notes/")
    suspend fun createNote(
        @Body request: CreateNoteRequestDto,
    ): Response<StatusResponseDto>

    @PATCH("notes/{noteId}")
    suspend fun updateNote(
        @Path("noteId") noteId: String,
        @Body request: UpdateNoteRequestDto,
    ): Response<StatusResponseDto>

    @DELETE("notes/{noteId}")
    suspend fun deleteNote(
        @Path("noteId") noteId: String,
    ): Response<Unit>
}
