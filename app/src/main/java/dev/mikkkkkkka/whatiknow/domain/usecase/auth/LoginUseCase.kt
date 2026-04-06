package dev.mikkkkkkka.whatiknow.domain.usecase.auth

import dev.mikkkkkkka.whatiknow.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(username: String, password: String) {
        repository.login(username, password)
    }
}
