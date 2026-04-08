package com.buidlsta.stagebuisla.ui.screens.suppliers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.db.entity.SupplierEntity
import com.buidlsta.stagebuisla.data.repository.SupplierRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SuppliersState(
    val suppliers: List<SupplierEntity> = emptyList(),
    val isLoading: Boolean = true
)

class SuppliersViewModel(private val repo: SupplierRepository) : ViewModel() {

    private val _state = MutableStateFlow(SuppliersState())
    val state: StateFlow<SuppliersState> = _state.asStateFlow()

    private val _editSupplier = MutableStateFlow<SupplierEntity?>(null)
    val editSupplier: StateFlow<SupplierEntity?> = _editSupplier.asStateFlow()

    init {
        viewModelScope.launch {
            repo.getAll().collect { suppliers ->
                _state.update { it.copy(suppliers = suppliers, isLoading = false) }
            }
        }
    }

    fun loadEditSupplier(id: Long) {
        viewModelScope.launch {
            _editSupplier.value = repo.getById(id)
        }
    }

    fun addSupplier(name: String, contact: String, email: String, phone: String, address: String) {
        viewModelScope.launch {
            repo.insert(SupplierEntity(name = name.trim(), contact = contact.trim(), email = email.trim(), phone = phone.trim(), address = address.trim()))
        }
    }

    fun updateSupplier(id: Long, name: String, contact: String, email: String, phone: String, address: String) {
        viewModelScope.launch {
            repo.update(SupplierEntity(id = id, name = name.trim(), contact = contact.trim(), email = email.trim(), phone = phone.trim(), address = address.trim()))
        }
    }

    fun deleteSupplier(id: Long) {
        viewModelScope.launch { repo.deleteById(id) }
    }
}
