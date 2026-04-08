package com.buidlsta.stagebuisla.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.preferences.AppPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileState(
    val userName: String = "Builder",
    val userEmail: String = "",
    val isLoading: Boolean = true
)

class ProfileViewModel(private val prefs: AppPreferences) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(prefs.userName, prefs.userEmail) { name, email ->
                ProfileState(userName = name, userEmail = email, isLoading = false)
            }.collect { _state.value = it }
        }
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            prefs.setUserName(name)
            prefs.setUserEmail(email)
        }
    }
}
