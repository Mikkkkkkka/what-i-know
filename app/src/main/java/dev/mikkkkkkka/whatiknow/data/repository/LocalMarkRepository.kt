package dev.mikkkkkkka.whatiknow.data.repository

import dev.mikkkkkkka.whatiknow.data.local.dao.MarkDao
import dev.mikkkkkkka.whatiknow.data.mapper.RoomMarkEntityMapper
import dev.mikkkkkkka.whatiknow.domain.model.Mark
import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository
import java.time.LocalDate
import java.time.LocalDateTime

class LocalMarkRepository(
    private val markDao: MarkDao, private val mapper: RoomMarkEntityMapper
) : MarkRepository {

    override suspend fun getMarksInPeriod(
        from: LocalDate, to: LocalDate
    ): List<Mark> {
        return markDao.getAllInPeriod(from, to).map {
            mapper.map(it)
        }
    }

    override suspend fun getMark(date: LocalDate): Mark? {
        val markEntity = markDao.getByDate(date)
        return markEntity?.let { mapper.map(it) }
    }

    override suspend fun saveMark(mark: Mark) {
        val updatedAt = LocalDateTime.now()
        val markEntity = mapper.unmap(mark, updatedAt)
        when (markDao.getByDate(mark.date)) {
            null -> markDao.insert(markEntity)
            else -> markDao.update(markEntity)
        }
    }

    override suspend fun deleteMark(date: LocalDate) {
        markDao.delete(date)
    }

    override suspend fun sync() {
        // no external sync happening
    }
}