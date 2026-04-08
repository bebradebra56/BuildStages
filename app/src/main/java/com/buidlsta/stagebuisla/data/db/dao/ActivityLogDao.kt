package com.buidlsta.stagebuisla.data.db.dao

import androidx.room.*
import com.buidlsta.stagebuisla.data.db.entity.ActivityLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_log ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM activity_log WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getByProject(projectId: Long): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM activity_log ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: ActivityLogEntity): Long

    @Query("DELETE FROM activity_log WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM activity_log")
    suspend fun deleteAll()
}
