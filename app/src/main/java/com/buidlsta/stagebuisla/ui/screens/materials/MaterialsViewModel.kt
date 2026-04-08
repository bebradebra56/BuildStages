package com.buidlsta.stagebuisla.ui.screens.materials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.db.entity.MaterialEntity
import com.buidlsta.stagebuisla.data.repository.MaterialRepository
import com.buidlsta.stagebuisla.data.repository.PhaseRepository
import com.buidlsta.stagebuisla.data.db.entity.PhaseEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MaterialsState(
    val materials: List<MaterialEntity> = emptyList(),
    val phases: List<PhaseEntity> = emptyList(),
    val totalCost: Double = 0.0,
    val isLoading: Boolean = true
)

class MaterialsViewModel(
    private val materialRepo: MaterialRepository,
    private val phaseRepo: PhaseRepository,
    private val projectId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(MaterialsState())
    val state: StateFlow<MaterialsState> = _state.asStateFlow()

    private val _editMaterial = MutableStateFlow<MaterialEntity?>(null)
    val editMaterial: StateFlow<MaterialEntity?> = _editMaterial.asStateFlow()

    private val _filterPhaseId = MutableStateFlow(0L)

    init {
        viewModelScope.launch {
            phaseRepo.getByProject(projectId).collect { phases ->
                _state.update { it.copy(phases = phases) }
            }
        }
        viewModelScope.launch {
            _filterPhaseId.flatMapLatest { phaseId ->
                if (phaseId != 0L) materialRepo.getByPhase(phaseId)
                else materialRepo.getByProject(projectId)
            }.collect { materials ->
                val total = materials.sumOf { it.quantity * it.unitCost }
                _state.update { it.copy(materials = materials, totalCost = total, isLoading = false) }
            }
        }
    }

    fun setPhaseFilter(phaseId: Long) {
        _filterPhaseId.value = phaseId
    }

    fun loadEditMaterial(id: Long) {
        viewModelScope.launch {
            _editMaterial.value = materialRepo.getById(id)
        }
    }

    fun addMaterial(
        name: String,
        quantity: Double,
        unit: String,
        unitCost: Double,
        status: String,
        phaseId: Long
    ) {
        viewModelScope.launch {
            materialRepo.insert(
                MaterialEntity(
                    phaseId = phaseId,
                    projectId = projectId,
                    name = name.trim(),
                    quantity = quantity,
                    unit = unit,
                    unitCost = unitCost,
                    status = status
                )
            )
        }
    }

    fun updateMaterial(
        id: Long,
        name: String,
        quantity: Double,
        unit: String,
        unitCost: Double,
        status: String,
        phaseId: Long
    ) {
        viewModelScope.launch {
            materialRepo.update(
                MaterialEntity(
                    id = id,
                    phaseId = phaseId,
                    projectId = projectId,
                    name = name.trim(),
                    quantity = quantity,
                    unit = unit,
                    unitCost = unitCost,
                    status = status
                )
            )
        }
    }

    fun deleteMaterial(id: Long) {
        viewModelScope.launch {
            materialRepo.deleteById(id)
        }
    }
}
