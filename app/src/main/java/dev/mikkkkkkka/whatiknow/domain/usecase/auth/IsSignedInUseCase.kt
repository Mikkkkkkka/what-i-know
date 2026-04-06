package dev.mikkkkkkka.whatiknow.domain.usecase.auth

import dev.mikkkkkkka.whatiknow.domain.repository.AuthRepository
import javax.inject.Inject

class IsSignedInUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    operator fun invoke(): Boolean {
        return repository.isSignedIn()
    }
}
