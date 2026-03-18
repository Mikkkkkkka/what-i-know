package dev.mikkkkkkka.whatiknow.domain.usecase.mark

import dev.mikkkkkkka.whatiknow.di.AppModule
import dev.mikkkkkkka.whatiknow.domain.model.Mark
import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository

class SaveMarkUseCase(
    private val repository: MarkRepository
) {
    suspend operator fun invoke(mark: Mark) {
        repository.saveMark(mark)
    }

    companion object {
        fun create(appModule: AppModule): SaveMarkUseCase {
            return SaveMarkUseCase(appModule.markRepository)
        }
    }
}

