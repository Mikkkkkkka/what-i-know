package dev.mikkkkkkka.whatiknow.data.repository

import dev.mikkkkkkka.whatiknow.data.remote.ApiErrorParser
import dev.mikkkkkkka.whatiknow.data.remote.api.AuthApi
import dev.mikkkkkkka.whatiknow.data.remote.dto.LoginRequestDto
import dev.mikkkkkkka.whatiknow.data.remote.dto.RegisterRequestDto
import dev.mikkkkkkka.whatiknow.data.session.AuthSessionStore
import dev.mikkkkkkka.whatiknow.domain.repository.AuthRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sessionStore: AuthSessionStore,
    private val errorParser: ApiErrorParser,
) : AuthRepository {
    override suspend fun login(username: String, password: String) {
        val normalizedUsername = username.trim()
        val response = authApi.login(
            LoginRequestDto(
                username = normalizedUsername,
                password = password,
            )
        )
        if (!response.isSuccessful) {
            throw IOException(errorParser.message(response))
        }

        val token = response.body()?.token.orEmpty()
        if (token.isBlank()) {
            throw IOException("Server returned an empty token")
        }

        sessionStore.save(normalizedUsername, token)
    }

    override suspend fun register(username: String, password: String) {
        val normalizedUsername = username.trim()
        val response = authApi.register(
            RegisterRequestDto(
                username = normalizedUsername,
                password = password,
            )
        )
        if (!response.isSuccessful) {
            throw IOException(errorParser.message(response))
        }
    }

    override fun isSignedIn(): Boolean = sessionStore.isSignedIn()

    override fun currentUsername(): String? = sessionStore.username()

    override fun logout() {
        sessionStore.clear()
    }
}
