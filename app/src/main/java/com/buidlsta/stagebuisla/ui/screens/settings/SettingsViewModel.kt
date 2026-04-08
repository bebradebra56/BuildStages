package com.buidlsta.stagebuisla.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.preferences.AppPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsState(
    val themeMode: String = "System",
    val currencySymbol: String = "$",
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = true
)

class SettingsViewModel(private val prefs: AppPreferences) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(prefs.themeMode, prefs.currencySymbol, prefs.notificationsEnabled) { theme, currency, notif ->
                SettingsState(themeMode = theme, currencySymbol = currency, notificationsEnabled = notif, isLoading = false)
            }.collect { _state.value = it }
        }
    }

    fun setThemeMode(mode: String) { viewModelScope.launch { prefs.setThemeMode(mode) } }
    fun setCurrencySymbol(symbol: String) { viewModelScope.launch { prefs.setCurrencySymbol(symbol) } }
    fun setNotificationsEnabled(enabled: Boolean) { viewModelScope.launch { prefs.setNotificationsEnabled(enabled) } }
}
