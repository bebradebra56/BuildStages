package com.buidlsta.stagebuisla.ui.screens.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.navigation.NavController
import com.buidlsta.stagebuisla.data.db.entity.ActivityLogEntity
import com.buidlsta.stagebuisla.ui.components.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityHistoryScreen(navController: NavController) {
    val vm: ActivityViewModel = koinViewModel()
    val state by vm.state.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Activity History",
                onBack = { navController.popBackStack() },
                actions = {
                    if (state.logs.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.isLoading) { LoadingScreen(); return@Scaffold }
        if (state.logs.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.History, "No activity yet", "Actions on projects will be recorded here")
            }
            return@Scaffold
        }
        LazyColumn(
            contentPadding = PaddingValues(top = innerPadding.calculateTopPadding() + 8.dp, bottom = innerPadding.calculateBottomPadding() + 16.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.logs, key = { it.id }) { log ->
                ActivityLogItem(log = log)
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear History") },
            text = { Text("Are you sure you want to clear all activity history?") },
            confirmButton = {
                TextButton(
                    onClick = { vm.clearAll(); showClearDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Clear All") }
            },
            dismissButton = { TextButton(onClick = { showClearDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun ActivityLogItem(log: ActivityLogEntity) {
    val (icon, color) = when {
        log.action.contains("Created") -> Icons.Default.Add to MaterialTheme.colorScheme.tertiary
        log.action.contains("Updated") -> Icons.Default.Edit to MaterialTheme.colorScheme.secondary
        log.action.contains("Deleted") -> Icons.Default.Delete to MaterialTheme.colorScheme.error
        log.action.contains("Completed") -> Icons.Default.CheckCircle to MaterialTheme.colorScheme.tertiary
        else -> Icons.Default.Info to MaterialTheme.colorScheme.primary
    }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(log.action, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                if (log.details.isNotEmpty()) {
                    Text(log.details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(formatDateTime(log.timestamp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
