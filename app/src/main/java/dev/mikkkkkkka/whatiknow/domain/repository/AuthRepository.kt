package dev.mikkkkkkka.whatiknow.domain.repository

interface AuthRepository {
    suspend fun login(username: String, password: String)

    suspend fun register(username: String, password: String)

    fun isSignedIn(): Boolean

    fun currentUsername(): String?

    fun logout()
}
