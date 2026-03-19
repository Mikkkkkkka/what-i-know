package dev.mikkkkkkka.whatiknow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.mikkkkkkka.whatiknow.data.local.entities.NoteEntity

@Dao
interface NoteDao {
    @Query("SELECT id FROM NoteEntity")
    suspend fun getAllIds(): List<String>

    @Query("SELECT * FROM NoteEntity WHERE id = :id")
    suspend fun getById(id: String): NoteEntity?

    @Insert
    suspend fun insert(note: NoteEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Query("DELETE FROM NoteEntity WHERE id = :id")
    suspend fun delete(id: String)
}

