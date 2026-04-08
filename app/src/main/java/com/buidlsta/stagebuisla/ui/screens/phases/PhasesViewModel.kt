package com.buidlsta.stagebuisla.ui.screens.phases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.db.entity.PhaseEntity
import com.buidlsta.stagebuisla.data.db.entity.ProjectEntity
import com.buidlsta.stagebuisla.data.repository.PhaseRepository
import com.buidlsta.stagebuisla.data.repository.ProjectRepository
import com.buidlsta.stagebuisla.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PhasesState(
    val project: ProjectEntity? = null,
    val phases: List<PhaseEntity> = emptyList(),
    val isLoading: Boolean = true
)

class PhasesViewModel(
    private val phaseRepo: PhaseRepository,
    private val projectRepo: ProjectRepository,
    private val taskRepo: TaskRepository,
    private val projectId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(PhasesState())
    val state: StateFlow<PhasesState> = _state.asStateFlow()

    private val _editPhase = MutableStateFlow<PhaseEntity?>(null)
    val editPhase: StateFlow<PhaseEntity?> = _editPhase.asStateFlow()

    init {
        viewModelScope.launch {
            val project = projectRepo.getById(projectId)
            _state.update { it.copy(project = project) }
        }
        viewModelScope.launch {
            phaseRepo.getByProject(projectId).collect { phases ->
                _state.update { it.copy(phases = phases, isLoading = false) }
            }
        }
    }

    fun loadEditPhase(phaseId: Long) {
        viewModelScope.launch {
            _editPhase.value = phaseRepo.getById(phaseId)
        }
    }

    fun addPhase(name: String, description: String, startDate: Long, endDate: Long) {
        viewModelScope.launch {
            val currentCount = phaseRepo.getTotalCount(projectId)
            phaseRepo.insert(
                PhaseEntity(
                    projectId = projectId,
                    name = name.trim(),
                    description = description.trim(),
                    orderIndex = currentCount,
                    startDate = startDate,
                    endDate = endDate,
                    status = "Pending"
                )
            )
        }
    }

    fun updatePhase(
        id: Long,
        name: String,
        description: String,
        startDate: Long,
        endDate: Long,
        status: String,
        progress: Int
    ) {
        viewModelScope.launch {
            phaseRepo.update(
                PhaseEntity(
                    id = id,
                    projectId = projectId,
                    name = name.trim(),
                    description = description.trim(),
                    startDate = startDate,
                    endDate = endDate,
                    status = status,
                    progress = progress
                )
            )
        }
    }

    fun deletePhase(id: Long) {
        viewModelScope.launch {
            phaseRepo.deleteById(id)
        }
    }

    fun updatePhaseProgress(phaseId: Long, progress: Int) {
        viewModelScope.launch {
            val phase = phaseRepo.getById(phaseId) ?: return@launch
            phaseRepo.update(phase.copy(progress = progress, status = when {
                progress == 100 -> "Completed"
                progress > 0 -> "InProgress"
                else -> "Pending"
            }))
        }
    }
}
