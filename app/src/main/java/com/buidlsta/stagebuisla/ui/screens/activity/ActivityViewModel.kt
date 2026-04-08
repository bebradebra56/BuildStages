package com.buidlsta.stagebuisla.ui.screens.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.db.entity.ActivityLogEntity
import com.buidlsta.stagebuisla.data.repository.ActivityLogRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ActivityState(
    val logs: List<ActivityLogEntity> = emptyList(),
    val isLoading: Boolean = true
)

class ActivityViewModel(private val repo: ActivityLogRepository) : ViewModel() {
    private val _state = MutableStateFlow(ActivityState())
    val state: StateFlow<ActivityState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repo.getAll().collect { logs ->
                _state.update { it.copy(logs = logs, isLoading = false) }
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch { repo.deleteAll() }
    }
}
