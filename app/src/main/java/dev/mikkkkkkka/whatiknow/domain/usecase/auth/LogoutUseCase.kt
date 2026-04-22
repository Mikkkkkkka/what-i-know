package dev.mikkkkkkka.whatiknow.domain.usecase.auth

import dev.mikkkkkkka.whatiknow.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    operator fun invoke() {
        repository.logout()
    }
}
