package dev.mikkkkkkka.whatiknow.domain.usecase.mark

import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository
import javax.inject.Inject

class SyncMarksUseCase @Inject constructor(
    private val repository: MarkRepository
) {
    suspend operator fun invoke() {
        return repository.sync()
    }
}

