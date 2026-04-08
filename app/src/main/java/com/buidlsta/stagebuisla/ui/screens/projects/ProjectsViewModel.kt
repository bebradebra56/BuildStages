package com.buidlsta.stagebuisla.ui.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.db.entity.ProjectEntity
import com.buidlsta.stagebuisla.data.preferences.AppPreferences
import com.buidlsta.stagebuisla.data.repository.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ProjectsState(
    val projects: List<ProjectEntity> = emptyList(),
    val isLoading: Boolean = true
)

class ProjectsViewModel(
    private val repo: ProjectRepository,
    private val prefs: AppPreferences,
    private val editProjectId: Long = 0L
) : ViewModel() {

    private val _state = MutableStateFlow(ProjectsState())
    val state: StateFlow<ProjectsState> = _state.asStateFlow()

    private val _editProject = MutableStateFlow<ProjectEntity?>(null)
    val editProject: StateFlow<ProjectEntity?> = _editProject.asStateFlow()

    init {
        viewModelScope.launch {
            repo.getAll().collect { projects ->
                _state.update { it.copy(projects = projects, isLoading = false) }
            }
        }
        if (editProjectId != 0L) {
            viewModelScope.launch {
                _editProject.value = repo.getById(editProjectId)
            }
        }
    }

    fun addProject(
        name: String,
        type: String,
        description: String,
        startDate: Long,
        endDate: Long,
        totalBudget: Double
    ) {
        viewModelScope.launch {
            val id = repo.insert(
                ProjectEntity(
                    name = name.trim(),
                    type = type,
                    description = description.trim(),
                    startDate = startDate,
                    endDate = endDate,
                    totalBudget = totalBudget,
                    status = "InProgress"
                )
            )
            prefs.setActiveProjectId(id)
        }
    }

    fun updateProject(
        id: Long,
        name: String,
        type: String,
        description: String,
        startDate: Long,
        endDate: Long,
        totalBudget: Double,
        status: String
    ) {
        viewModelScope.launch {
            repo.update(
                ProjectEntity(
                    id = id,
                    name = name.trim(),
                    type = type,
                    description = description.trim(),
                    startDate = startDate,
                    endDate = endDate,
                    totalBudget = totalBudget,
                    status = status
                )
            )
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            repo.deleteById(id)
        }
    }
}
