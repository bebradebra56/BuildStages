package com.buidlsta.stagebuisla.data.repository

import com.buidlsta.stagebuisla.data.db.dao.NotificationDao
import com.buidlsta.stagebuisla.data.db.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDao: NotificationDao) {
    fun getAll(): Flow<List<NotificationEntity>> = notificationDao.getAll()

    fun getUnreadCount(): Flow<Int> = notificationDao.getUnreadCount()

    suspend fun insert(notification: NotificationEntity): Long = notificationDao.insert(notification)

    suspend fun markAsRead(id: Long) = notificationDao.markAsRead(id)

    suspend fun markAllAsRead() = notificationDao.markAllAsRead()

    suspend fun deleteById(id: Long) = notificationDao.deleteById(id)

    suspend fun deleteAll() = notificationDao.deleteAll()
}
