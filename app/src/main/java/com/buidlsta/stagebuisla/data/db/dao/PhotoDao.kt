package com.buidlsta.stagebuisla.data.db.dao

import androidx.room.*
import com.buidlsta.stagebuisla.data.db.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos ORDER BY timestamp DESC")
    fun getAll(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getByProject(projectId: Long): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE phaseId = :phaseId ORDER BY timestamp DESC")
    fun getByPhase(phaseId: Long): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE projectId = :projectId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentByProject(projectId: Long, limit: Int): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun getById(id: Long): PhotoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: PhotoEntity): Long

    @Update
    suspend fun update(photo: PhotoEntity)

    @Delete
    suspend fun delete(photo: PhotoEntity)

    @Query("DELETE FROM photos WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM photos WHERE phaseId = :phaseId")
    suspend fun deleteByPhase(phaseId: Long)

    @Query("DELETE FROM photos WHERE projectId = :projectId")
    suspend fun deleteByProject(projectId: Long)
}
