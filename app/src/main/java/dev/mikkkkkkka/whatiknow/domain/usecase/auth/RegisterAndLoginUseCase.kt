package dev.mikkkkkkka.whatiknow.domain.usecase.auth

import dev.mikkkkkkka.whatiknow.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterAndLoginUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(username: String, password: String) {
        repository.register(username, password)
        repository.login(username, password)
    }
}
