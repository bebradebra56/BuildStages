package com.buidlsta.stagebuisla.data.db.dao

import androidx.room.*
import com.buidlsta.stagebuisla.data.db.entity.PhaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhaseDao {
    @Query("SELECT * FROM phases WHERE projectId = :projectId ORDER BY orderIndex ASC")
    fun getByProject(projectId: Long): Flow<List<PhaseEntity>>

    @Query("SELECT * FROM phases WHERE id = :id")
    suspend fun getById(id: Long): PhaseEntity?

    @Query("SELECT * FROM phases WHERE projectId = :projectId ORDER BY orderIndex ASC")
    suspend fun getByProjectSync(projectId: Long): List<PhaseEntity>

    @Query("SELECT COUNT(*) FROM phases WHERE projectId = :projectId AND status = 'Completed'")
    suspend fun getCompletedCount(projectId: Long): Int

    @Query("SELECT COUNT(*) FROM phases WHERE projectId = :projectId")
    suspend fun getTotalCount(projectId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(phase: PhaseEntity): Long

    @Update
    suspend fun update(phase: PhaseEntity)

    @Delete
    suspend fun delete(phase: PhaseEntity)

    @Query("DELETE FROM phases WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM phases WHERE projectId = :projectId")
    suspend fun deleteByProject(projectId: Long)
}
