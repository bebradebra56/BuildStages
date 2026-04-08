package com.buidlsta.stagebuisla.data.repository

import com.buidlsta.stagebuisla.data.db.dao.EquipmentDao
import com.buidlsta.stagebuisla.data.db.entity.EquipmentEntity
import kotlinx.coroutines.flow.Flow

class EquipmentRepository(private val equipmentDao: EquipmentDao) {
    fun getAll(): Flow<List<EquipmentEntity>> = equipmentDao.getAll()

    fun getByPhase(phaseId: Long): Flow<List<EquipmentEntity>> = equipmentDao.getByPhase(phaseId)

    suspend fun getById(id: Long): EquipmentEntity? = equipmentDao.getById(id)

    suspend fun insert(equipment: EquipmentEntity): Long = equipmentDao.insert(equipment)

    suspend fun update(equipment: EquipmentEntity) = equipmentDao.update(equipment)

    suspend fun deleteById(id: Long) = equipmentDao.deleteById(id)
}
