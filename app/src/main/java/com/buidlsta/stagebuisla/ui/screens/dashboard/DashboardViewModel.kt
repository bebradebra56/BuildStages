package com.buidlsta.stagebuisla.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.db.entity.*
import com.buidlsta.stagebuisla.data.preferences.AppPreferences
import com.buidlsta.stagebuisla.data.repository.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private data class DashboardCore(
    val projects: List<ProjectEntity>,
    val activeProject: ProjectEntity?,
    val upcoming: List<TaskEntity>,
    val activeCount: Int,
    val doneCount: Int
)

data class DashboardState(
    val projects: List<ProjectEntity> = emptyList(),
    val activeProject: ProjectEntity? = null,
    val upcomingTasks: List<TaskEntity> = emptyList(),
    val recentPhotos: List<PhotoEntity> = emptyList(),
    val totalProjects: Int = 0,
    val activeTasks: Int = 0,
    val completedTasks: Int = 0,
    val userName: String = "Builder",
    val isLoading: Boolean = true
)

class DashboardViewModel(
    private val projectRepo: ProjectRepository,
    private val taskRepo: TaskRepository,
    private val photoRepo: PhotoRepository,
    private val activityRepo: ActivityLogRepository,
    private val prefs: AppPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadDashboard() {
        viewModelScope.launch {
            combine(
                projectRepo.getAll(),
                projectRepo.getActiveProject(),
                taskRepo.getUpcoming(5),
                taskRepo.observeActiveCountGlobal(),
                taskRepo.observeCompletedCountGlobal()
            ) { projects, activeProject, upcoming, activeCount, doneCount ->
                DashboardCore(
                    projects = projects,
                    activeProject = activeProject,
                    upcoming = upcoming,
                    activeCount = activeCount,
                    doneCount = doneCount
                )
            }.combine(prefs.userName) { core, userName ->
                DashboardState(
                    projects = core.projects,
                    activeProject = core.activeProject ?: core.projects.firstOrNull(),
                    upcomingTasks = core.upcoming,
                    totalProjects = core.projects.size,
                    activeTasks = core.activeCount,
                    completedTasks = core.doneCount,
                    userName = userName,
                    isLoading = false
                )
            }.collect { newState ->
                _state.update { current ->
                    newState.copy(recentPhotos = current.recentPhotos)
                }
            }
        }

        viewModelScope.launch {
            projectRepo.getActiveProject()
                .flatMapLatest { activeProject ->
                    val id = activeProject?.id ?: 0L
                    if (id != 0L) photoRepo.getRecentByProject(id, 6)
                    else flowOf(emptyList())
                }
                .collect { photos ->
                    _state.update { it.copy(recentPhotos = photos) }
                }
        }
    }

    fun setActiveProject(projectId: Long) {
        viewModelScope.launch {
            prefs.setActiveProjectId(projectId)
        }
    }
}
