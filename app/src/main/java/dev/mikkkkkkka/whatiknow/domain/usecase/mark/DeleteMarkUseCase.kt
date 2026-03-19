package dev.mikkkkkkka.whatiknow.domain.usecase.mark

import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository
import java.time.LocalDate
import javax.inject.Inject

class DeleteMarkUseCase @Inject constructor(
    private val repository: MarkRepository
) {
    suspend operator fun invoke(date: LocalDate) {
        repository.deleteMark(date)
    }
}

