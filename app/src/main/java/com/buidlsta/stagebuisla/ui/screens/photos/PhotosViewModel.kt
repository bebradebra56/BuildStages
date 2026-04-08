package com.buidlsta.stagebuisla.ui.screens.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.db.entity.PhaseEntity
import com.buidlsta.stagebuisla.data.db.entity.PhotoEntity
import com.buidlsta.stagebuisla.data.repository.PhaseRepository
import com.buidlsta.stagebuisla.data.repository.PhotoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PhotosState(
    val photos: List<PhotoEntity> = emptyList(),
    val phases: List<PhaseEntity> = emptyList(),
    val selectedPhaseId: Long = 0L,
    val isLoading: Boolean = true
)

class PhotosViewModel(
    private val photoRepo: PhotoRepository,
    private val phaseRepo: PhaseRepository,
    private val projectId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(PhotosState())
    val state: StateFlow<PhotosState> = _state.asStateFlow()

    private val _filterPhaseId = MutableStateFlow(0L)

    init {
        viewModelScope.launch {
            phaseRepo.getByProject(projectId).collect { phases ->
                _state.update { it.copy(phases = phases) }
            }
        }
        viewModelScope.launch {
            _filterPhaseId.flatMapLatest { phaseId ->
                if (phaseId != 0L) photoRepo.getByPhase(phaseId)
                else photoRepo.getByProject(projectId)
            }.collect { photos ->
                _state.update { it.copy(photos = photos, isLoading = false) }
            }
        }
    }

    fun setPhaseFilter(phaseId: Long) {
        _filterPhaseId.value = phaseId
        _state.update { it.copy(selectedPhaseId = phaseId) }
    }

    fun savePhoto(uri: String, caption: String, phaseId: Long) {
        viewModelScope.launch {
            photoRepo.insert(
                PhotoEntity(
                    phaseId = phaseId,
                    projectId = projectId,
                    uri = uri,
                    caption = caption
                )
            )
        }
    }

    fun deletePhoto(id: Long) {
        viewModelScope.launch {
            photoRepo.deleteById(id)
        }
    }
}
