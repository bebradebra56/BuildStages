package com.buidlsta.stagebuisla.data.repository

import com.buidlsta.stagebuisla.data.db.dao.MaterialDao
import com.buidlsta.stagebuisla.data.db.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow

class MaterialRepository(private val materialDao: MaterialDao) {
    fun getByProject(projectId: Long): Flow<List<MaterialEntity>> = materialDao.getByProject(projectId)

    fun getByPhase(phaseId: Long): Flow<List<MaterialEntity>> = materialDao.getByPhase(phaseId)

    suspend fun getById(id: Long): MaterialEntity? = materialDao.getById(id)

    suspend fun getTotalMaterialCost(projectId: Long): Double =
        materialDao.getTotalMaterialCost(projectId) ?: 0.0

    suspend fun insert(material: MaterialEntity): Long = materialDao.insert(material)

    suspend fun update(material: MaterialEntity) = materialDao.update(material)

    suspend fun deleteById(id: Long) = materialDao.deleteById(id)
}
