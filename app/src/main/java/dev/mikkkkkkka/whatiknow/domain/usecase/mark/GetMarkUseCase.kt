package dev.mikkkkkkka.whatiknow.domain.usecase.mark

import dev.mikkkkkkka.whatiknow.domain.model.Mark
import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository
import java.time.LocalDate
import javax.inject.Inject

class GetMarkUseCase @Inject constructor(
    private val repository: MarkRepository
) {
    suspend operator fun invoke(date: LocalDate): Mark? {
        return repository.getMark(date)
    }
}

