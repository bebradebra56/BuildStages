package com.buidlsta.stagebuisla.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.db.entity.TaskEntity
import com.buidlsta.stagebuisla.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class CalendarState(
    val selectedDate: Long = System.currentTimeMillis(),
    val tasksForDay: List<TaskEntity> = emptyList(),
    val taskDates: Set<Long> = emptySet(),
    val allTasks: List<TaskEntity> = emptyList(),
    val isLoading: Boolean = true
)

class CalendarViewModel(private val taskRepo: TaskRepository) : ViewModel() {

    private val _state = MutableStateFlow(CalendarState())
    val state: StateFlow<CalendarState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            taskRepo.getAll().collect { tasks ->
                val dates = tasks
                    .filter { it.deadline != 0L }
                    .map { normalizeDate(it.deadline) }
                    .toSet()
                _state.update {
                    it.copy(
                        allTasks = tasks,
                        taskDates = dates,
                        isLoading = false
                    )
                }
                loadTasksForDate(_state.value.selectedDate)
            }
        }
    }

    fun selectDate(dateMillis: Long) {
        _state.update { it.copy(selectedDate = dateMillis) }
        loadTasksForDate(dateMillis)
    }

    private fun loadTasksForDate(dateMillis: Long) {
        viewModelScope.launch {
            val start = startOfDay(dateMillis)
            val end = endOfDay(dateMillis)
            taskRepo.getByDate(start, end).collect { tasks ->
                _state.update { it.copy(tasksForDay = tasks) }
            }
        }
    }

    private fun normalizeDate(millis: Long): Long {
        return startOfDay(millis)
    }

    private fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun endOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }
}
