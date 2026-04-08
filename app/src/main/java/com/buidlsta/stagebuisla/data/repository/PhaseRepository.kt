package com.buidlsta.stagebuisla.data.repository

import com.buidlsta.stagebuisla.data.db.dao.ActivityLogDao
import com.buidlsta.stagebuisla.data.db.dao.PhaseDao
import com.buidlsta.stagebuisla.data.db.entity.ActivityLogEntity
import com.buidlsta.stagebuisla.data.db.entity.PhaseEntity
import kotlinx.coroutines.flow.Flow

class PhaseRepository(
    private val phaseDao: PhaseDao,
    private val activityLogDao: ActivityLogDao
) {
    fun getByProject(projectId: Long): Flow<List<PhaseEntity>> = phaseDao.getByProject(projectId)

    suspend fun getById(id: Long): PhaseEntity? = phaseDao.getById(id)

    suspend fun getByProjectSync(projectId: Long): List<PhaseEntity> = phaseDao.getByProjectSync(projectId)

    suspend fun getCompletedCount(projectId: Long): Int = phaseDao.getCompletedCount(projectId)

    suspend fun getTotalCount(projectId: Long): Int = phaseDao.getTotalCount(projectId)

    suspend fun insert(phase: PhaseEntity): Long {
        val id = phaseDao.insert(phase)
        activityLogDao.insert(
            ActivityLogEntity(
                projectId = phase.projectId,
                action = "Phase Added",
                details = "Added phase: ${phase.name}"
            )
        )
        return id
    }

    suspend fun update(phase: PhaseEntity) {
        phaseDao.update(phase)
        activityLogDao.insert(
            ActivityLogEntity(
                projectId = phase.projectId,
                action = "Phase Updated",
                details = "Updated phase: ${phase.name}"
            )
        )
    }

    suspend fun delete(phase: PhaseEntity) {
        phaseDao.delete(phase)
    }

    suspend fun deleteById(id: Long) {
        phaseDao.deleteById(id)
    }
}
