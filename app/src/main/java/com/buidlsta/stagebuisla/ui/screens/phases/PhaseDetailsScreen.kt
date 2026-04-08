package com.buidlsta.stagebuisla.ui.screens.phases

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.buidlsta.stagebuisla.data.db.entity.MaterialEntity
import com.buidlsta.stagebuisla.data.db.entity.PhaseEntity
import com.buidlsta.stagebuisla.data.db.entity.TaskEntity
import com.buidlsta.stagebuisla.data.repository.MaterialRepository
import com.buidlsta.stagebuisla.data.repository.PhaseRepository
import com.buidlsta.stagebuisla.data.repository.TaskRepository
import com.buidlsta.stagebuisla.ui.components.*
import com.buidlsta.stagebuisla.ui.navigation.Screen
import com.buidlsta.stagebuisla.ui.theme.StatusCompleted
import com.buidlsta.stagebuisla.ui.theme.StatusInProgress
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

data class PhaseDetailsState(
    val phase: PhaseEntity? = null,
    val tasks: List<TaskEntity> = emptyList(),
    val materials: List<MaterialEntity> = emptyList(),
    val isLoading: Boolean = true
)

class PhaseDetailsViewModel(
    private val phaseRepo: PhaseRepository,
    private val taskRepo: TaskRepository,
    private val materialRepo: MaterialRepository,
    private val phaseId: Long
) : ViewModel() {
    private val _state = MutableStateFlow(PhaseDetailsState())
    val state: StateFlow<PhaseDetailsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val phase = phaseRepo.getById(phaseId)
            _state.update { it.copy(phase = phase) }
        }
        viewModelScope.launch {
            combine(
                taskRepo.getByPhase(phaseId),
                materialRepo.getByPhase(phaseId)
            ) { tasks, materials -> tasks to materials }
                .collect { (tasks, materials) ->
                    _state.update { it.copy(tasks = tasks, materials = materials, isLoading = false) }
                }
        }
    }

    fun toggleTaskStatus(task: TaskEntity) {
        viewModelScope.launch {
            val newStatus = when (task.status) {
                "Todo" -> "InProgress"
                "InProgress" -> "Done"
                else -> "Todo"
            }
            taskRepo.update(task.copy(status = newStatus))
        }
    }
}

@Composable
fun PhaseDetailsScreen(navController: NavController, phaseId: Long) {
    val vm: PhaseDetailsViewModel = koinViewModel(parameters = { parametersOf(phaseId) })
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = state.phase?.name ?: "Phase Details",
                onBack = { navController.popBackStack() },
                actions = {
                    state.phase?.let { phase ->
                        IconButton(onClick = {
                            navController.navigate(Screen.AddEditPhase.route(phase.projectId, phaseId))
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            state.phase?.let { phase ->
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.AddEditTask.route(phase.projectId, phaseId))
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        }
    ) { innerPadding ->
        if (state.isLoading) {
            LoadingScreen()
            return@Scaffold
        }

        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.phase?.let { phase ->
                item {
                    PhaseHeaderCard(phase = phase, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
            }

            item {
                SectionHeader(
                    title = "Tasks (${state.tasks.size})",
                    action = "Add",
                    onAction = {
                        state.phase?.let { phase ->
                            navController.navigate(Screen.AddEditTask.route(phase.projectId, phaseId))
                        }
                    }
                )
            }

            if (state.tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No tasks yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(state.tasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        onToggle = { vm.toggleTaskStatus(task) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item {
                SectionHeader(
                    title = "Materials (${state.materials.size})",
                    action = "Add",
                    onAction = {
                        state.phase?.let { phase ->
                            navController.navigate(Screen.AddEditMaterial.route(phase.projectId, phaseId))
                        }
                    }
                )
            }

            if (state.materials.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No materials yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(state.materials, key = { it.id }) { material ->
                    MaterialRow(
                        material = material,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item {
                state.phase?.let { phase ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { navController.navigate(Screen.Photos.route(phase.projectId, phaseId)) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Photos")
                        }
                        OutlinedButton(
                            onClick = { navController.navigate(Screen.Materials.route(phase.projectId, phaseId)) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Inventory, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Materials")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhaseHeaderCard(phase: PhaseEntity, modifier: Modifier = Modifier) {
    val statusColor = when (phase.status) {
        "Completed" -> StatusCompleted
        "InProgress" -> StatusInProgress
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(phase.status)
                if (phase.endDate != 0L) {
                    Text(
                        text = "Due: ${formatDate(phase.endDate)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (phase.description.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = phase.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            ProgressBar(progress = phase.progress, color = statusColor)
        }
    }
}

@Composable
private fun TaskRow(task: TaskEntity, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggle, modifier = Modifier.size(32.dp)) {
                Icon(
                    when (task.status) {
                        "Done" -> Icons.Default.CheckCircle
                        "InProgress" -> Icons.Default.PlayCircle
                        else -> Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = null,
                    tint = when (task.status) {
                        "Done" -> StatusCompleted
                        "InProgress" -> StatusInProgress
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (task.deadline != 0L) {
                    Text(
                        text = formatDate(task.deadline),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            PriorityChip(task.priority)
        }
    }
}

@Composable
private fun MaterialRow(material: MaterialEntity, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Inventory,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${material.quantity} ${material.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusChip(material.status)
        }
    }
}
