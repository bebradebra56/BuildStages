package com.buidlsta.stagebuisla.ui.screens.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.data.db.entity.ExpenseEntity
import com.buidlsta.stagebuisla.data.db.entity.ProjectEntity
import com.buidlsta.stagebuisla.data.repository.ExpenseRepository
import com.buidlsta.stagebuisla.data.repository.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BudgetState(
    val project: ProjectEntity? = null,
    val expenses: List<ExpenseEntity> = emptyList(),
    val totalBudget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val remaining: Double = 0.0,
    val spentByCategory: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = true
)

class BudgetViewModel(
    private val expenseRepo: ExpenseRepository,
    private val projectRepo: ProjectRepository,
    private val projectId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(BudgetState())
    val state: StateFlow<BudgetState> = _state.asStateFlow()

    private val _editExpense = MutableStateFlow<ExpenseEntity?>(null)
    val editExpense: StateFlow<ExpenseEntity?> = _editExpense.asStateFlow()

    init {
        viewModelScope.launch {
            val project = projectRepo.getById(projectId)
            _state.update { it.copy(project = project, totalBudget = project?.totalBudget ?: 0.0) }
        }
        viewModelScope.launch {
            expenseRepo.getByProject(projectId).collect { expenses ->
                val total = expenses.sumOf { it.amount }
                val byCategory = expenses.groupBy { it.category }
                    .mapValues { (_, v) -> v.sumOf { it.amount } }
                val budget = _state.value.totalBudget
                _state.update {
                    it.copy(
                        expenses = expenses,
                        totalSpent = total,
                        remaining = budget - total,
                        spentByCategory = byCategory,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun loadEditExpense(id: Long) {
        viewModelScope.launch {
            _editExpense.value = expenseRepo.getById(id)
        }
    }

    fun addExpense(description: String, amount: Double, category: String, date: Long, phaseId: Long) {
        viewModelScope.launch {
            expenseRepo.insert(
                ExpenseEntity(
                    projectId = projectId,
                    phaseId = phaseId,
                    description = description.trim(),
                    amount = amount,
                    date = date,
                    category = category
                )
            )
        }
    }

    fun updateExpense(id: Long, description: String, amount: Double, category: String, date: Long, phaseId: Long) {
        viewModelScope.launch {
            expenseRepo.update(
                ExpenseEntity(
                    id = id,
                    projectId = projectId,
                    phaseId = phaseId,
                    description = description.trim(),
                    amount = amount,
                    date = date,
                    category = category
                )
            )
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            expenseRepo.deleteById(id)
        }
    }
}
