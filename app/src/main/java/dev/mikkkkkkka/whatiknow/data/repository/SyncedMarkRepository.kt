package dev.mikkkkkkka.whatiknow.data.repository

import dev.mikkkkkkka.whatiknow.data.local.dao.MarkDao
import dev.mikkkkkkka.whatiknow.data.local.dao.PendingDeletionDao
import dev.mikkkkkkka.whatiknow.data.local.entities.MarkEntity
import dev.mikkkkkkka.whatiknow.data.local.entities.PendingDeletionEntity
import dev.mikkkkkkka.whatiknow.data.mapper.RoomMarkEntityMapper
import dev.mikkkkkkka.whatiknow.data.remote.api.MarksApi
import dev.mikkkkkkka.whatiknow.data.session.AuthSessionStore
import dev.mikkkkkkka.whatiknow.domain.model.Mark
import dev.mikkkkkkka.whatiknow.domain.repository.MarkRepository
import java.time.LocalDate
import javax.inject.Inject

class SyncedMarkRepository @Inject constructor(
    private val markDao: MarkDao,
    private val mapper: RoomMarkEntityMapper,
    private val pendingDeletionDao: PendingDeletionDao,
    private val marksApi: MarksApi,
    private val sessionStore: AuthSessionStore,
) : MarkRepository {

    override suspend fun getMarksInPeriod(from: LocalDate, to: LocalDate): List<Mark> {
        return markDao.getAllInPeriod(from, to).map(mapper::map)
    }

    override suspend fun getMark(date: LocalDate): Mark? = markDao.getByDate(date)?.let(mapper::map)

    override suspend fun saveMark(mark: Mark) {
        val entity = mapper.unmap(mark, nowUtc())
        markDao.upsert(entity)
        pendingDeletionDao.delete(TYPE_MARK, mark.id)

        if (sessionStore.isSignedIn()) {
            runCatching { upsertRemote(entity) }
        }
    }

    override suspend fun deleteMark(date: LocalDate) {
        val currentMark = markDao.getByDate(date)
        markDao.delete(date)

        val markId = currentMark?.id ?: return
        pendingDeletionDao.upsert(
            PendingDeletionEntity(
                itemType = TYPE_MARK,
                itemId = markId,
                deletedAt = nowUtc(),
            )
        )

        if (sessionStore.isSignedIn()) {
            runCatching {
                deleteRemote(markId)
                pendingDeletionDao.delete(TYPE_MARK, markId)
            }
        }
    }

    override suspend fun sync() {
        if (!sessionStore.isSignedIn()) {
            return
        }

        syncPendingDeletions()

        val localMarks = markDao.getAll().associateBy { it.id }.toMutableMap()
        val remoteMarks = marksApi.listMarks(AuthSessionStore.USER_ROUTE_ID)
            .map { it.toEntity() }
            .groupBy { it.date }
            .mapValues { (_, marks) -> marks.maxBy { it.updatedAt } }
            .values
            .associateBy { it.id }
            .toMutableMap()
        val deletedIds = pendingDeletionDao.getByType(TYPE_MARK).map { it.itemId }.toSet()

        for ((id, localMark) in localMarks) {
            val remoteMark = remoteMarks.remove(id)
            if (remoteMark == null) {
                runCatching { upsertRemote(localMark) }
                continue
            }

            if (localMark.updatedAt >= remoteMark.updatedAt) {
                if (localMark.content != remoteMark.content || localMark.date != remoteMark.date) {
                    runCatching { upsertRemote(localMark) }
                }
            } else {
                markDao.upsert(remoteMark)
            }
        }

        remoteMarks.values
            .filterNot { it.id in deletedIds }
            .forEach { markDao.upsert(it) }
    }

    private suspend fun syncPendingDeletions() {
        pendingDeletionDao.getByType(TYPE_MARK).forEach { deletion ->
            runCatching {
                deleteRemote(deletion.itemId)
                pendingDeletionDao.delete(TYPE_MARK, deletion.itemId)
            }
        }
    }

    private suspend fun upsertRemote(mark: MarkEntity) {
        val createResponse = marksApi.createMark(mark.toCreateRequest())
        if (createResponse.isSuccessful) {
            return
        }
        if (createResponse.code() == 409) {
            marksApi.updateMark(mark.id, mark.toUpdateRequest())
        }
    }

    private suspend fun deleteRemote(id: String) {
        val response = marksApi.deleteMark(id)
        if (response.isSuccessful || response.code() == 404) {
            return
        }
        error("Delete mark failed with code ${response.code()}")
    }

    private companion object {
        const val TYPE_MARK = "mark"
    }
}
