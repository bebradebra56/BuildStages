package com.buidlsta.stagebuisla.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.db.entity.NotificationEntity
import com.buidlsta.stagebuisla.data.repository.NotificationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class NotificationsState(
    val notifications: List<NotificationEntity> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = true
)

class NotificationsViewModel(private val repo: NotificationRepository) : ViewModel() {
    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(repo.getAll(), repo.getUnreadCount()) { notifications, unread ->
                notifications to unread
            }.collect { (notifications, unread) ->
                _state.update { it.copy(notifications = notifications, unreadCount = unread, isLoading = false) }
            }
        }
    }

    fun markAsRead(id: Long) { viewModelScope.launch { repo.markAsRead(id) } }
    fun markAllAsRead() { viewModelScope.launch { repo.markAllAsRead() } }
    fun delete(id: Long) { viewModelScope.launch { repo.deleteById(id) } }
    fun clearAll() { viewModelScope.launch { repo.deleteAll() } }
}
