package dev.mikkkkkkka.whatiknow.domain.repository

import dev.mikkkkkkka.whatiknow.domain.model.Mark
import java.time.LocalDate

interface MarkRepository {
    suspend fun getMarksInPeriod(from: LocalDate, to: LocalDate): List<Mark>

    suspend fun getMark(date: LocalDate): Mark?

    suspend fun saveMark(mark: Mark)

    suspend fun sync()
}