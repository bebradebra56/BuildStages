package com.buidlsta.stagebuisla.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String = "Residential",
    val description: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = 0L,
    val totalBudget: Double = 0.0,
    val status: String = "Planning",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "phases")
data class PhaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val name: String,
    val description: String = "",
    val orderIndex: Int = 0,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val status: String = "Pending",
    val progress: Int = 0
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phaseId: Long,
    val projectId: Long,
    val name: String,
    val description: String = "",
    val deadline: Long = 0L,
    val priority: String = "Medium",
    val status: String = "Todo",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phaseId: Long,
    val projectId: Long,
    val name: String,
    val quantity: Double = 0.0,
    val unit: String = "pcs",
    val unitCost: Double = 0.0,
    val supplierId: Long = 0L,
    val status: String = "Needed"
)

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phaseId: Long,
    val projectId: Long,
    val uri: String,
    val caption: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val phaseId: Long = 0L,
    val description: String,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val category: String = "Other"
)

@Entity(tableName = "suppliers")
data class SupplierEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val contact: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = ""
)

@Entity(tableName = "equipment")
data class EquipmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String = "",
    val status: String = "Available",
    val assignedPhaseId: Long = 0L
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "Info"
)

@Entity(tableName = "activity_log")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long = 0L,
    val action: String,
    val details: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
