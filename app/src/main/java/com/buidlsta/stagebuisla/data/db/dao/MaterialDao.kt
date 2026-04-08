package com.buidlsta.stagebuisla.data.db.dao

import androidx.room.*
import com.buidlsta.stagebuisla.data.db.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Query("SELECT * FROM materials WHERE projectId = :projectId ORDER BY name ASC")
    fun getByProject(projectId: Long): Flow<List<MaterialEntity>>

    @Query("SELECT * FROM materials WHERE phaseId = :phaseId ORDER BY name ASC")
    fun getByPhase(phaseId: Long): Flow<List<MaterialEntity>>

    @Query("SELECT * FROM materials WHERE id = :id")
    suspend fun getById(id: Long): MaterialEntity?

    @Query("SELECT SUM(quantity * unitCost) FROM materials WHERE projectId = :projectId")
    suspend fun getTotalMaterialCost(projectId: Long): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(material: MaterialEntity): Long

    @Update
    suspend fun update(material: MaterialEntity)

    @Delete
    suspend fun delete(material: MaterialEntity)

    @Query("DELETE FROM materials WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM materials WHERE phaseId = :phaseId")
    suspend fun deleteByPhase(phaseId: Long)

    @Query("DELETE FROM materials WHERE projectId = :projectId")
    suspend fun deleteByProject(projectId: Long)
}
