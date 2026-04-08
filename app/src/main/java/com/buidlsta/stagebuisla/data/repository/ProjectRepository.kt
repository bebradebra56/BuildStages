package com.buidlsta.stagebuisla.data.repository

import com.buidlsta.stagebuisla.data.db.dao.ActivityLogDao
import com.buidlsta.stagebuisla.data.db.dao.ProjectDao
import com.buidlsta.stagebuisla.data.db.entity.ActivityLogEntity
import com.buidlsta.stagebuisla.data.db.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(
    private val projectDao: ProjectDao,
    private val activityLogDao: ActivityLogDao
) {
    fun getAll(): Flow<List<ProjectEntity>> = projectDao.getAll()

    fun getActiveProject(): Flow<ProjectEntity?> = projectDao.getActiveProject()

    suspend fun getById(id: Long): ProjectEntity? = projectDao.getById(id)

    suspend fun insert(project: ProjectEntity): Long {
        val id = projectDao.insert(project)
        activityLogDao.insert(
            ActivityLogEntity(
                projectId = id,
                action = "Project Created",
                details = "Created project: ${project.name}"
            )
        )
        return id
    }

    suspend fun update(project: ProjectEntity) {
        projectDao.update(project)
        activityLogDao.insert(
            ActivityLogEntity(
                projectId = project.id,
                action = "Project Updated",
                details = "Updated project: ${project.name}"
            )
        )
    }

    suspend fun delete(project: ProjectEntity) {
        projectDao.delete(project)
        activityLogDao.insert(
            ActivityLogEntity(
                action = "Project Deleted",
                details = "Deleted project: ${project.name}"
            )
        )
    }

    suspend fun deleteById(id: Long) {
        val project = projectDao.getById(id) ?: return
        delete(project)
    }
}
