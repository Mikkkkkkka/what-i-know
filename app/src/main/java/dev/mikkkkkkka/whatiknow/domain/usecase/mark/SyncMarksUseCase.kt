package dev.mikkkkkkka.whatiknow.domain.usecase.mark

import dev.mikkkkkkka.whatiknow.di.AppModule
import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository

class SyncMarksUseCase(
    private val repository: MarkRepository
) {
    suspend operator fun invoke() {
        return repository.sync()
    }

    companion object {
        fun create(appModule: AppModule): SyncMarksUseCase {
            return SyncMarksUseCase(appModule.markRepository)
        }
    }
}

