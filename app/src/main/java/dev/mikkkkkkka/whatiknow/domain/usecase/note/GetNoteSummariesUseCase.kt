package dev.mikkkkkkka.whatiknow.domain.usecase.note

import dev.mikkkkkkka.whatiknow.domain.model.NoteSummary
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNoteSummariesUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    operator fun invoke(): Flow<List<NoteSummary>> {
        return repository.getNoteSummaries()
    }
}
