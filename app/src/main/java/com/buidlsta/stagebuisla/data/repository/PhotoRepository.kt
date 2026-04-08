package com.buidlsta.stagebuisla.data.repository

import com.buidlsta.stagebuisla.data.db.dao.PhotoDao
import com.buidlsta.stagebuisla.data.db.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

class PhotoRepository(private val photoDao: PhotoDao) {
    fun getAll(): Flow<List<PhotoEntity>> = photoDao.getAll()

    fun getByProject(projectId: Long): Flow<List<PhotoEntity>> = photoDao.getByProject(projectId)

    fun getByPhase(phaseId: Long): Flow<List<PhotoEntity>> = photoDao.getByPhase(phaseId)

    fun getRecentByProject(projectId: Long, limit: Int = 6): Flow<List<PhotoEntity>> =
        photoDao.getRecentByProject(projectId, limit)

    suspend fun getById(id: Long): PhotoEntity? = photoDao.getById(id)

    suspend fun insert(photo: PhotoEntity): Long = photoDao.insert(photo)

    suspend fun update(photo: PhotoEntity) = photoDao.update(photo)

    suspend fun deleteById(id: Long) = photoDao.deleteById(id)
}
