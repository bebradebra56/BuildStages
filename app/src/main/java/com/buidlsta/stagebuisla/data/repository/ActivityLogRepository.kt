package com.buidlsta.stagebuisla.data.repository

import com.buidlsta.stagebuisla.data.db.dao.ActivityLogDao
import com.buidlsta.stagebuisla.data.db.entity.ActivityLogEntity
import kotlinx.coroutines.flow.Flow

class ActivityLogRepository(private val activityLogDao: ActivityLogDao) {
    fun getAll(): Flow<List<ActivityLogEntity>> = activityLogDao.getAll()

    fun getByProject(projectId: Long): Flow<List<ActivityLogEntity>> =
        activityLogDao.getByProject(projectId)

    fun getRecent(limit: Int = 20): Flow<List<ActivityLogEntity>> = activityLogDao.getRecent(limit)

    suspend fun insert(log: ActivityLogEntity): Long = activityLogDao.insert(log)

    suspend fun deleteAll() = activityLogDao.deleteAll()
}
