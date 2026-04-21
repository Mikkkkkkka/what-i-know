package dev.mikkkkkkka.whatiknow.data.repository

import dev.mikkkkkkka.whatiknow.data.local.dao.NoteDao
import dev.mikkkkkkka.whatiknow.data.local.dao.PendingDeletionDao
import dev.mikkkkkkka.whatiknow.data.local.entities.NoteEntity
import dev.mikkkkkkka.whatiknow.data.local.entities.PendingDeletionEntity
import dev.mikkkkkkka.whatiknow.data.mapper.RoomNoteEntityMapper
import dev.mikkkkkkka.whatiknow.data.remote.api.NotesApi
import dev.mikkkkkkka.whatiknow.data.session.AuthSessionStore
import dev.mikkkkkkka.whatiknow.domain.model.Note
import dev.mikkkkkkka.whatiknow.domain.model.NoteSummary
import dev.mikkkkkkka.whatiknow.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SyncedNoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val mapper: RoomNoteEntityMapper,
    private val pendingDeletionDao: PendingDeletionDao,
    private val notesApi: NotesApi,
    private val sessionStore: AuthSessionStore,
) : NoteRepository {

    override fun getNoteSummaries(): Flow<List<NoteSummary>> {
        return noteDao.observeAll().map { notes ->
            notes.map { note ->
                NoteSummary(
                    id = note.id,
                    name = note.name,
                )
            }
        }
    }

    override suspend fun getNoteIds(): List<String> = noteDao.getAllIds()

    override suspend fun getNoteNames(): List<String> = noteDao.getAllNames()

    override suspend fun getNote(id: String): Note? = noteDao.getById(id)?.let(mapper::map)

    override suspend fun saveNote(note: Note) {
        val entity = mapper.unmap(note, nowUtc())
        noteDao.upsert(entity)
        pendingDeletionDao.delete(TYPE_NOTE, note.id)

        if (sessionStore.isSignedIn()) {
            runCatching { upsertRemote(entity) }
        }
    }

    override suspend fun deleteNote(id: String) {
        noteDao.delete(id)
        if (id.isBlank()) {
            return
        }

        pendingDeletionDao.upsert(
            PendingDeletionEntity(
                itemType = TYPE_NOTE,
                itemId = id,
                deletedAt = nowUtc(),
            )
        )

        if (sessionStore.isSignedIn()) {
            runCatching {
                deleteRemote(id)
                pendingDeletionDao.delete(TYPE_NOTE, id)
            }
        }
    }

    override suspend fun sync() {
        if (!sessionStore.isSignedIn()) {
            return
        }

        syncPendingDeletions()

        val localNotes = noteDao.getAll()
        val remoteNotes = notesApi.listNotes(AuthSessionStore.USER_ROUTE_ID)
            .associateBy { it.id }
            .toMutableMap()
        val deletedIds = pendingDeletionDao.getByType(TYPE_NOTE).map { it.itemId }.toSet()

        for (localNote in localNotes) {
            val remoteNote = remoteNotes.remove(localNote.id)?.toEntity()
            if (remoteNote == null) {
                runCatching { upsertRemote(localNote) }
                continue
            }

            if (localNote.updatedAt >= remoteNote.updatedAt) {
                if (localNote.name != remoteNote.name || localNote.content != remoteNote.content) {
                    runCatching { upsertRemote(localNote) }
                }
            } else {
                noteDao.upsert(remoteNote)
            }
        }

        remoteNotes.values
            .map { it.toEntity() }
            .filterNot { it.id in deletedIds }
            .forEach { noteDao.upsert(it) }
    }

    private suspend fun syncPendingDeletions() {
        pendingDeletionDao.getByType(TYPE_NOTE).forEach { deletion ->
            runCatching {
                deleteRemote(deletion.itemId)
                pendingDeletionDao.delete(TYPE_NOTE, deletion.itemId)
            }
        }
    }

    private suspend fun upsertRemote(note: NoteEntity) {
        val createResponse = notesApi.createNote(note.toCreateRequest())
        if (createResponse.isSuccessful) {
            return
        }
        if (createResponse.code() == 409) {
            notesApi.updateNote(note.id, note.toUpdateRequest())
        }
    }

    private suspend fun deleteRemote(id: String) {
        val response = notesApi.deleteNote(id)
        if (response.isSuccessful || response.code() == 404) {
            return
        }
        error("Delete note failed with code ${response.code()}")
    }

    private companion object {
        const val TYPE_NOTE = "note"
    }
}
