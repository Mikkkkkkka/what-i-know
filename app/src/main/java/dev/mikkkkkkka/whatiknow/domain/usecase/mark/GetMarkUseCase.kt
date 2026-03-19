package dev.mikkkkkkka.whatiknow.domain.usecase.mark

import dev.mikkkkkkka.whatiknow.di.AppModule
import dev.mikkkkkkka.whatiknow.domain.model.Mark
import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository
import java.time.LocalDate

class GetMarkUseCase(
    private val repository: MarkRepository
) {
    suspend operator fun invoke(date: LocalDate): Mark? {
        return repository.getMark(date)
    }

    companion object {
        fun create(appModule: AppModule): GetMarkUseCase {
            return GetMarkUseCase(appModule.markRepository)
        }
    }
}

