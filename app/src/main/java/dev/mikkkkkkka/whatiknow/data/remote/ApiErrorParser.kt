package dev.mikkkkkkka.whatiknow.data.remote

import com.google.gson.Gson
import dev.mikkkkkkka.whatiknow.data.remote.dto.ErrorResponseDto
import retrofit2.Response

class ApiErrorParser(
    private val gson: Gson,
) {
    fun message(response: Response<*>): String {
        val rawBody = response.errorBody()?.string().orEmpty()
        if (rawBody.isBlank()) {
            return "Request failed with code ${response.code()}"
        }

        return runCatching {
            gson.fromJson(rawBody, ErrorResponseDto::class.java)?.error
        }.getOrNull().orEmpty().ifBlank {
            "Request failed with code ${response.code()}"
        }
    }
}
