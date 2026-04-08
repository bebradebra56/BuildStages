package com.buidlsta.stagebuisla.ui.screens.equipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.db.entity.EquipmentEntity
import com.buidlsta.stagebuisla.data.repository.EquipmentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class EquipmentState(
    val equipment: List<EquipmentEntity> = emptyList(),
    val isLoading: Boolean = true
)

class EquipmentViewModel(private val repo: EquipmentRepository) : ViewModel() {

    private val _state = MutableStateFlow(EquipmentState())
    val state: StateFlow<EquipmentState> = _state.asStateFlow()

    private val _editEquipment = MutableStateFlow<EquipmentEntity?>(null)
    val editEquipment: StateFlow<EquipmentEntity?> = _editEquipment.asStateFlow()

    init {
        viewModelScope.launch {
            repo.getAll().collect { eq ->
                _state.update { it.copy(equipment = eq, isLoading = false) }
            }
        }
    }

    fun loadEditEquipment(id: Long) {
        viewModelScope.launch { _editEquipment.value = repo.getById(id) }
    }

    fun addEquipment(name: String, type: String, status: String) {
        viewModelScope.launch { repo.insert(EquipmentEntity(name = name.trim(), type = type.trim(), status = status)) }
    }

    fun updateEquipment(id: Long, name: String, type: String, status: String) {
        viewModelScope.launch { repo.update(EquipmentEntity(id = id, name = name.trim(), type = type.trim(), status = status)) }
    }

    fun deleteEquipment(id: Long) {
        viewModelScope.launch { repo.deleteById(id) }
    }
}
