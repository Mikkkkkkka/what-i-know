package dev.mikkkkkkka.whatiknow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.mikkkkkkka.whatiknow.data.local.entities.PendingDeletionEntity

@Dao
interface PendingDeletionDao {
    @Query("SELECT * FROM PendingDeletionEntity WHERE itemType = :itemType")
    suspend fun getByType(itemType: String): List<PendingDeletionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PendingDeletionEntity)

    @Query("DELETE FROM PendingDeletionEntity WHERE itemType = :itemType AND itemId = :itemId")
    suspend fun delete(itemType: String, itemId: String)
}
