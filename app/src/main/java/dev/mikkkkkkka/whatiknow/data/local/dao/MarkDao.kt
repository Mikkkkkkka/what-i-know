package dev.mikkkkkkka.whatiknow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.mikkkkkkka.whatiknow.data.local.entities.MarkEntity
import java.time.LocalDate

@Dao
interface MarkDao {
    @Query("SELECT * FROM MarkEntity")
    suspend fun getAll(): List<MarkEntity>

    @Query("SELECT * FROM MarkEntity WHERE :from <= date AND date < :to")
    suspend fun getAllInPeriod(from: LocalDate, to: LocalDate): List<MarkEntity>

    @Query("SELECT * FROM MarkEntity WHERE date = :date")
    suspend fun getByDate(date: LocalDate): MarkEntity?

    @Insert
    suspend fun insert(mark: MarkEntity)

    @Update
    suspend fun update(mark: MarkEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(mark: MarkEntity)

    @Query("DELETE FROM MarkEntity WHERE date = :date")
    suspend fun delete(date: LocalDate)
}

