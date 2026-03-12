package com.example.whatiknow.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {
    @Query("SELECT id FROM NoteEntity")
    fun getAllIds(): List<String>

    @Query("SELECT * FROM NoteEntity WHERE id = :id")
    fun getById(id: String): NoteEntity?

    @Insert
    suspend fun insert(note: NoteEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(id: String)
}
