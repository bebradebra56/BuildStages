package com.buidlsta.stagebuisla.ui.screens.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.buidlsta.stagebuisla.ui.components.*
import com.buidlsta.stagebuisla.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ReportsScreen(navController: NavController, projectId: Long) {
    val vm: ReportsViewModel = koinViewModel(parameters = { parametersOf(projectId) })
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = state.project?.name?.let { "$it — Reports" } ?: "Reports",
                onBack = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            LoadingScreen()
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
                Text("Progress Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Phases Done",
                        value = "${state.completedPhases}/${state.totalPhases}",
                        icon = Icons.Default.Layers,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Tasks Done",
                        value = "${state.completedTasks}/${state.totalTasks}",
                        icon = Icons.Default.CheckCircle,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                OverallProgressCard(state = state)
            }

            item {
                Text("Financial Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Budget",
                        value = formatCurrency(state.budget),
                        icon = Icons.Default.AccountBalance,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Spent",
                        value = formatCurrency(state.totalSpent),
                        icon = Icons.Default.Receipt,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (state.tasksByStatus.isNotEmpty()) {
                item {
                    Text("Tasks by Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                item {
                    TaskStatusChart(tasksByStatus = state.tasksByStatus, total = state.totalTasks)
                }
            }

            if (state.phases.isNotEmpty()) {
                item {
                    Text("Phase Progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                item {
                    PhasesProgressList(phases = state.phases)
                }
            }
        }
    }
}

@Composable
private fun OverallProgressCard(state: ReportsState) {
    val phaseProgress = if (state.totalPhases > 0) state.completedPhases.toFloat() / state.totalPhases else 0f
    val taskProgress = if (state.totalTasks > 0) state.completedTasks.toFloat() / state.totalTasks else 0f
    val budgetProgress = if (state.budget > 0) (state.totalSpent / state.budget).toFloat().coerceIn(0f, 1f) else 0f

    Card(shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Overall Progress", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

            ProgressRow("Phases completed", "${(phaseProgress * 100).toInt()}%", phaseProgress, StatusCompleted)
            ProgressRow("Tasks completed", "${(taskProgress * 100).toInt()}%", taskProgress, StatusInProgress)
            ProgressRow("Budget used", "${(budgetProgress * 100).toInt()}%", budgetProgress, if (budgetProgress > 0.9f) MaterialTheme.colorScheme.error else StatusOnHold)
        }
    }
}

@Composable
private fun ProgressRow(label: String, valueText: String, progress: Float, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text(valueText, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun TaskStatusChart(tasksByStatus: Map<String, Int>, total: Int) {
    val statusColors = mapOf(
        "Todo" to StatusPending,
        "InProgress" to StatusInProgress,
        "Done" to StatusCompleted
    )

    Card(shape = RoundedCornerShape(16.dp)) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(100.dp)) {
                var startAngle = -90f
                tasksByStatus.forEach { (status, count) ->
                    val sweep = count.toFloat() / total * 360f
                    drawArc(
                        color = statusColors[status] ?: StatusPending,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = true,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height)
                    )
                    startAngle += sweep
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tasksByStatus.forEach { (status, count) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(statusColors[status] ?: StatusPending))
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text(
                                text = when (status) { "InProgress" -> "In Progress"; else -> status },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "$count tasks (${if (total > 0) count * 100 / total else 0}%)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhasesProgressList(phases: List<com.buidlsta.stagebuisla.data.db.entity.PhaseEntity>) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            phases.forEach { phase ->
                val color = when (phase.status) {
                    "Completed" -> StatusCompleted
                    "InProgress" -> StatusInProgress
                    "OnHold" -> StatusOnHold
                    else -> StatusPending
                }
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(phase.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                        Text("${phase.progress}%", style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { phase.progress / 100f },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = color,
                        trackColor = color.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}
