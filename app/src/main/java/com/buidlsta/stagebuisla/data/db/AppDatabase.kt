package com.buidlsta.stagebuisla.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.buidlsta.stagebuisla.data.db.dao.*
import com.buidlsta.stagebuisla.data.db.entity.*

@Database(
    entities = [
        ProjectEntity::class,
        PhaseEntity::class,
        TaskEntity::class,
        MaterialEntity::class,
        PhotoEntity::class,
        ExpenseEntity::class,
        SupplierEntity::class,
        EquipmentEntity::class,
        NotificationEntity::class,
        ActivityLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun phaseDao(): PhaseDao
    abstract fun taskDao(): TaskDao
    abstract fun materialDao(): MaterialDao
    abstract fun photoDao(): PhotoDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun supplierDao(): SupplierDao
    abstract fun equipmentDao(): EquipmentDao
    abstract fun notificationDao(): NotificationDao
    abstract fun activityLogDao(): ActivityLogDao
}
