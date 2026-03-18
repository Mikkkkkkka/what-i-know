package dev.mikkkkkkka.whatiknow.domain.usecase.mark

import dev.mikkkkkkka.whatiknow.di.AppModule
import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository
import java.time.LocalDate

class DeleteMarkUseCase(
    private val repository: MarkRepository
) {
    suspend operator fun invoke(date: LocalDate) {
        repository.deleteMark(date)
    }

    companion object {
        fun create(appModule: AppModule): DeleteMarkUseCase {
            return DeleteMarkUseCase(appModule.markRepository)
        }
    }
}

