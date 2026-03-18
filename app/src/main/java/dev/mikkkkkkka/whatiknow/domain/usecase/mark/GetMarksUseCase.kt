package dev.mikkkkkkka.whatiknow.domain.usecase.mark

import dev.mikkkkkkka.whatiknow.di.AppModule
import dev.mikkkkkkka.whatiknow.domain.model.Mark
import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository
import java.time.LocalDate

class GetMarksUseCase(
    private val repository: MarkRepository
) {
    suspend operator fun invoke(from: LocalDate, to: LocalDate): List<Mark> {
        return repository.getMarksInPeriod(from, to)
    }

    companion object {
        fun create(appModule: AppModule): GetMarksUseCase {
            return GetMarksUseCase(appModule.markRepository)
        }
    }
}

