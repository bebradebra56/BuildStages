package com.buidlsta.stagebuisla.data.repository

import com.buidlsta.stagebuisla.data.db.dao.ActivityLogDao
import com.buidlsta.stagebuisla.data.db.dao.TaskDao
import com.buidlsta.stagebuisla.data.db.entity.ActivityLogEntity
import com.buidlsta.stagebuisla.data.db.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val activityLogDao: ActivityLogDao
) {
    fun getAll(): Flow<List<TaskEntity>> = taskDao.getAll()

    fun getByProject(projectId: Long): Flow<List<TaskEntity>> = taskDao.getByProject(projectId)

    fun getByPhase(phaseId: Long): Flow<List<TaskEntity>> = taskDao.getByPhase(phaseId)

    fun getUpcoming(limit: Int = 5): Flow<List<TaskEntity>> = taskDao.getUpcoming(limit)

    fun getByDate(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>> =
        taskDao.getByDate(startOfDay, endOfDay)

    suspend fun getById(id: Long): TaskEntity? = taskDao.getById(id)

    suspend fun getCompletedCount(projectId: Long): Int = taskDao.getCompletedCount(projectId)

    suspend fun getTotalCount(projectId: Long): Int = taskDao.getTotalCount(projectId)

    fun observeActiveCountGlobal(): Flow<Int> = taskDao.observeActiveCountGlobal()

    fun observeCompletedCountGlobal(): Flow<Int> = taskDao.observeCompletedCountGlobal()

    suspend fun insert(task: TaskEntity): Long {
        val id = taskDao.insert(task)
        activityLogDao.insert(
            ActivityLogEntity(
                projectId = task.projectId,
                action = "Task Created",
                details = "Created task: ${task.name}"
            )
        )
        return id
    }

    suspend fun update(task: TaskEntity) {
        taskDao.update(task)
        if (task.status == "Done") {
            activityLogDao.insert(
                ActivityLogEntity(
                    projectId = task.projectId,
                    action = "Task Completed",
                    details = "Completed task: ${task.name}"
                )
            )
        }
    }

    suspend fun deleteById(id: Long) = taskDao.deleteById(id)
}
