package com.buidlsta.stagebuisla.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.db.entity.*
import com.buidlsta.stagebuisla.data.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ReportsState(
    val project: ProjectEntity? = null,
    val phases: List<PhaseEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val materials: List<MaterialEntity> = emptyList(),
    val expenses: List<ExpenseEntity> = emptyList(),
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val totalPhases: Int = 0,
    val completedPhases: Int = 0,
    val totalSpent: Double = 0.0,
    val budget: Double = 0.0,
    val materialCost: Double = 0.0,
    val tasksByPriority: Map<String, Int> = emptyMap(),
    val tasksByStatus: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = true
)

class ReportsViewModel(
    private val projectRepo: ProjectRepository,
    private val phaseRepo: PhaseRepository,
    private val taskRepo: TaskRepository,
    private val materialRepo: MaterialRepository,
    private val expenseRepo: ExpenseRepository,
    private val projectId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(ReportsState())
    val state: StateFlow<ReportsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val project = projectRepo.getById(projectId)
            _state.update { it.copy(project = project, budget = project?.totalBudget ?: 0.0) }
        }
        viewModelScope.launch {
            combine(
                phaseRepo.getByProject(projectId),
                taskRepo.getByProject(projectId),
                materialRepo.getByProject(projectId),
                expenseRepo.getByProject(projectId)
            ) { phases, tasks, materials, expenses ->
                ReportsState(
                    project = _state.value.project,
                    phases = phases,
                    tasks = tasks,
                    materials = materials,
                    expenses = expenses,
                    totalTasks = tasks.size,
                    completedTasks = tasks.count { it.status == "Done" },
                    totalPhases = phases.size,
                    completedPhases = phases.count { it.status == "Completed" },
                    totalSpent = expenses.sumOf { it.amount },
                    budget = _state.value.budget,
                    materialCost = materials.sumOf { it.quantity * it.unitCost },
                    tasksByPriority = tasks.groupBy { it.priority }.mapValues { it.value.size },
                    tasksByStatus = tasks.groupBy { it.status }.mapValues { it.value.size },
                    isLoading = false
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}
