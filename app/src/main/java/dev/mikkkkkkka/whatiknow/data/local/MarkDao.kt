package dev.mikkkkkkka.whatiknow.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate

@Dao
interface MarkDao {
    @Query("SELECT * FROM MarkEntity")
    fun getAllIds(): List<MarkEntity>

    @Query("SELECT * FROM MarkEntity WHERE date = :date")
    fun getById(date: LocalDate): MarkEntity?

    @Insert
    suspend fun insert(mark: MarkEntity)

    @Update
    suspend fun update(mark: MarkEntity)

    @Delete
    suspend fun delete(mark: MarkEntity)
}

