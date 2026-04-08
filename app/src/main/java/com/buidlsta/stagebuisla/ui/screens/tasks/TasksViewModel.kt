package com.buidlsta.stagebuisla.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.db.entity.PhaseEntity
import com.buidlsta.stagebuisla.data.db.entity.TaskEntity
import com.buidlsta.stagebuisla.data.repository.PhaseRepository
import com.buidlsta.stagebuisla.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TasksState(
    val tasks: List<TaskEntity> = emptyList(),
    val phases: List<PhaseEntity> = emptyList(),
    val filterStatus: String = "All",
    val isLoading: Boolean = true
)

class TasksViewModel(
    private val taskRepo: TaskRepository,
    private val phaseId: Long,
    private val projectId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(TasksState())
    val state: StateFlow<TasksState> = _state.asStateFlow()

    private val _editTask = MutableStateFlow<TaskEntity?>(null)
    val editTask: StateFlow<TaskEntity?> = _editTask.asStateFlow()

    init {
        viewModelScope.launch {
            val flow = when {
                phaseId != 0L -> taskRepo.getByPhase(phaseId)
                projectId != 0L -> taskRepo.getByProject(projectId)
                else -> taskRepo.getAll()
            }
            flow.collect { tasks ->
                _state.update { it.copy(tasks = tasks, isLoading = false) }
            }
        }
    }

    fun loadEditTask(taskId: Long) {
        viewModelScope.launch {
            _editTask.value = taskRepo.getById(taskId)
        }
    }

    fun addTask(
        name: String,
        description: String,
        deadline: Long,
        priority: String,
        targetPhaseId: Long,
        targetProjectId: Long
    ) {
        viewModelScope.launch {
            taskRepo.insert(
                TaskEntity(
                    phaseId = targetPhaseId,
                    projectId = targetProjectId,
                    name = name.trim(),
                    description = description.trim(),
                    deadline = deadline,
                    priority = priority,
                    status = "Todo"
                )
            )
        }
    }

    fun updateTask(
        id: Long,
        name: String,
        description: String,
        deadline: Long,
        priority: String,
        status: String,
        phaseId: Long,
        projectId: Long
    ) {
        viewModelScope.launch {
            taskRepo.update(
                TaskEntity(
                    id = id,
                    phaseId = phaseId,
                    projectId = projectId,
                    name = name.trim(),
                    description = description.trim(),
                    deadline = deadline,
                    priority = priority,
                    status = status
                )
            )
        }
    }

    fun toggleStatus(task: TaskEntity) {
        viewModelScope.launch {
            val newStatus = when (task.status) {
                "Todo" -> "InProgress"
                "InProgress" -> "Done"
                else -> "Todo"
            }
            taskRepo.update(task.copy(status = newStatus))
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            taskRepo.deleteById(id)
        }
    }

    fun setFilter(status: String) {
        _state.update { it.copy(filterStatus = status) }
    }

    val filteredTasks: StateFlow<List<TaskEntity>> = state.map { s ->
        if (s.filterStatus == "All") s.tasks
        else s.tasks.filter { it.status == s.filterStatus }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
