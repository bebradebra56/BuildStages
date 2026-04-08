package com.buidlsta.stagebuisla.data.repository

import com.buidlsta.stagebuisla.data.db.dao.SupplierDao
import com.buidlsta.stagebuisla.data.db.entity.SupplierEntity
import kotlinx.coroutines.flow.Flow

class SupplierRepository(private val supplierDao: SupplierDao) {
    fun getAll(): Flow<List<SupplierEntity>> = supplierDao.getAll()

    suspend fun getById(id: Long): SupplierEntity? = supplierDao.getById(id)

    suspend fun insert(supplier: SupplierEntity): Long = supplierDao.insert(supplier)

    suspend fun update(supplier: SupplierEntity) = supplierDao.update(supplier)

    suspend fun deleteById(id: Long) = supplierDao.deleteById(id)
}
