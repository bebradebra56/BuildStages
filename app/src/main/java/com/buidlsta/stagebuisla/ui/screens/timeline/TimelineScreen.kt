package com.buidlsta.stagebuisla.ui.screens.timeline

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.buidlsta.stagebuisla.data.db.entity.PhaseEntity
import com.buidlsta.stagebuisla.data.db.entity.ProjectEntity
import com.buidlsta.stagebuisla.data.repository.PhaseRepository
import com.buidlsta.stagebuisla.data.repository.ProjectRepository
import com.buidlsta.stagebuisla.ui.components.*
import com.buidlsta.stagebuisla.ui.theme.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

data class TimelineState(
    val project: ProjectEntity? = null,
    val phases: List<PhaseEntity> = emptyList(),
    val isLoading: Boolean = true
)

class TimelineViewModel(
    private val projectRepo: ProjectRepository,
    private val phaseRepo: PhaseRepository,
    private val projectId: Long
) : ViewModel() {
    private val _state = MutableStateFlow(TimelineState())
    val state: StateFlow<TimelineState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val project = projectRepo.getById(projectId)
            _state.update { it.copy(project = project) }
        }
        viewModelScope.launch {
            phaseRepo.getByProject(projectId).collect { phases ->
                _state.update { it.copy(phases = phases, isLoading = false) }
            }
        }
    }
}

@Composable
fun TimelineScreen(navController: NavController, projectId: Long) {
    val vm: TimelineViewModel = koinViewModel(parameters = { parametersOf(projectId) })
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Timeline",
                onBack = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            LoadingScreen()
            return@Scaffold
        }

        if (state.phases.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Default.Timeline,
                    title = "No timeline data",
                    subtitle = "Add phases with dates to see the timeline"
                )
            }
            return@Scaffold
        }

        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                state.project?.let { project ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = project.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Start: ${formatDate(project.startDate)}", style = MaterialTheme.typography.bodySmall)
                                if (project.endDate != 0L) {
                                    Text("End: ${formatDate(project.endDate)}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Phase Timeline",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(state.phases.withIndex().toList()) { (index, phase) ->
                TimelinePhaseCard(phase = phase, index = index, totalPhases = state.phases.size)
            }

            item {
                GanttChart(phases = state.phases)
            }
        }
    }
}

@Composable
private fun TimelinePhaseCard(phase: PhaseEntity, index: Int, totalPhases: Int) {
    val statusColor = when (phase.status) {
        "Completed" -> StatusCompleted
        "InProgress" -> StatusInProgress
        "OnHold" -> StatusOnHold
        else -> StatusPending
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(32.dp)) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(statusColor),
                contentAlignment = Alignment.Center
            ) {
                if (phase.status == "Completed") {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text("${index + 1}", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            if (index < totalPhases - 1) {
                Box(
                    modifier = Modifier.width(2.dp).height(32.dp)
                        .background(if (phase.status == "Completed") statusColor else MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(phase.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    StatusChip(phase.status)
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (phase.startDate != 0L) {
                        Text(
                            "▶ ${formatDate(phase.startDate)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (phase.endDate != 0L) {
                        Text(
                            "■ ${formatDate(phase.endDate)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { phase.progress / 100f },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = statusColor,
                    trackColor = statusColor.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
private fun GanttChart(phases: List<PhaseEntity>) {
    val phasesWithDates = phases.filter { it.startDate != 0L && it.endDate != 0L }
    if (phasesWithDates.isEmpty()) return

    Card(shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Gantt Chart",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))

            val minDate = phasesWithDates.minOf { it.startDate }
            val maxDate = phasesWithDates.maxOf { it.endDate }
            val totalDuration = (maxDate - minDate).coerceAtLeast(1)

            phasesWithDates.forEach { phase ->
                val startOffset = (phase.startDate - minDate).toFloat() / totalDuration
                val duration = (phase.endDate - phase.startDate).toFloat() / totalDuration
                val statusColor = when (phase.status) {
                    "Completed" -> StatusCompleted
                    "InProgress" -> StatusInProgress
                    else -> StatusPending
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = phase.name,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.width(80.dp),
                        maxLines = 1
                    )
                    Spacer(Modifier.width(8.dp))
                    BoxWithConstraints(modifier = Modifier.weight(1f)) {
                        val barWidth = maxWidth
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.width(barWidth * startOffset))
                            Box(
                                modifier = Modifier
                                    .width(barWidth * duration.coerceAtLeast(0.02f))
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(statusColor)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatDate(minDate), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatDate(maxDate), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
