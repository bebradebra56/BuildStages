package com.buidlsta.stagebuisla.data.db.dao

import androidx.room.*
import com.buidlsta.stagebuisla.data.db.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY deadline ASC")
    fun getByProject(projectId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE phaseId = :phaseId ORDER BY deadline ASC")
    fun getByPhase(phaseId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE status != 'Done' ORDER BY deadline ASC LIMIT :limit")
    fun getUpcoming(limit: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE deadline > 0 AND deadline BETWEEN :startOfDay AND :endOfDay ORDER BY deadline ASC")
    fun getByDate(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE projectId = :projectId AND status = 'Done'")
    suspend fun getCompletedCount(projectId: Long): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE projectId = :projectId")
    suspend fun getTotalCount(projectId: Long): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE status != 'Done'")
    fun observeActiveCountGlobal(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE status = 'Done'")
    fun observeCompletedCountGlobal(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM tasks WHERE phaseId = :phaseId")
    suspend fun deleteByPhase(phaseId: Long)

    @Query("DELETE FROM tasks WHERE projectId = :projectId")
    suspend fun deleteByProject(projectId: Long)
}
