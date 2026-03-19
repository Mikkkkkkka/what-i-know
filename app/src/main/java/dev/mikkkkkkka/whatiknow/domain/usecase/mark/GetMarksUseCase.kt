package dev.mikkkkkkka.whatiknow.domain.usecase.mark

import dev.mikkkkkkka.whatiknow.domain.model.Mark
import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository
import java.time.LocalDate
import javax.inject.Inject

class GetMarksUseCase @Inject constructor(
    private val repository: MarkRepository
) {
    suspend operator fun invoke(from: LocalDate, to: LocalDate): List<Mark> {
        return repository.getMarksInPeriod(from, to)
    }
}

