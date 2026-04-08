package com.buidlsta.stagebuisla.data.repository

import com.buidlsta.stagebuisla.data.db.dao.ExpenseDao
import com.buidlsta.stagebuisla.data.db.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    fun getByProject(projectId: Long): Flow<List<ExpenseEntity>> = expenseDao.getByProject(projectId)

    suspend fun getById(id: Long): ExpenseEntity? = expenseDao.getById(id)

    suspend fun getTotalSpent(projectId: Long): Double = expenseDao.getTotalSpent(projectId) ?: 0.0

    suspend fun getSpentByCategory(projectId: Long, category: String): Double =
        expenseDao.getSpentByCategory(projectId, category) ?: 0.0

    suspend fun insert(expense: ExpenseEntity): Long = expenseDao.insert(expense)

    suspend fun update(expense: ExpenseEntity) = expenseDao.update(expense)

    suspend fun deleteById(id: Long) = expenseDao.deleteById(id)
}
