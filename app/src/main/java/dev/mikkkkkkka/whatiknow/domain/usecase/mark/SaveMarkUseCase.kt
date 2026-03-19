package dev.mikkkkkkka.whatiknow.domain.usecase.mark

import dev.mikkkkkkka.whatiknow.domain.model.Mark
import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository
import javax.inject.Inject

class SaveMarkUseCase @Inject constructor(
    private val repository: MarkRepository
) {
    suspend operator fun invoke(mark: Mark) {
        repository.saveMark(mark)
    }
}

