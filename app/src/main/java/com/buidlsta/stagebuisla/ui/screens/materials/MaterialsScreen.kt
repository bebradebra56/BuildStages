@file:OptIn(ExperimentalMaterial3Api::class)
package com.buidlsta.stagebuisla.ui.screens.materials

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.navigation.NavController
import com.buidlsta.stagebuisla.data.db.entity.MaterialEntity
import com.buidlsta.stagebuisla.ui.components.*
import com.buidlsta.stagebuisla.ui.navigation.Screen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialsScreen(navController: NavController, projectId: Long, phaseId: Long = 0L) {
    val vm: MaterialsViewModel = koinViewModel(parameters = { parametersOf(projectId) })
    val state by vm.state.collectAsState()
    var deleteTarget by remember { mutableStateOf<MaterialEntity?>(null) }
    var selectedPhaseId by remember { mutableStateOf(phaseId) }

    LaunchedEffect(selectedPhaseId) { vm.setPhaseFilter(selectedPhaseId) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Materials",
                onBack = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val pid = if (selectedPhaseId != 0L) selectedPhaseId
                    else state.phases.firstOrNull()?.id ?: 0L
                    navController.navigate(Screen.AddEditMaterial.route(projectId, pid))
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Material")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            if (state.phases.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedPhaseId == 0L,
                            onClick = { selectedPhaseId = 0L },
                            label = { Text("All") }
                        )
                    }
                    items(state.phases) { phase ->
                        FilterChip(
                            selected = selectedPhaseId == phase.id,
                            onClick = { selectedPhaseId = phase.id },
                            label = { Text(phase.name) }
                        )
                    }
                }
            }

            if (state.materials.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Inventory,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Total Material Cost",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = formatCurrency(state.totalCost),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "${state.materials.size} items",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (state.isLoading) {
                LoadingScreen()
                return@Column
            }

            if (state.materials.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = Icons.Default.Inventory,
                        title = "No materials yet",
                        subtitle = "Track materials like Concrete, Bricks, Wood..."
                    )
                }
                return@Column
            }

            LazyColumn(
                contentPadding = PaddingValues(
                    bottom = innerPadding.calculateBottomPadding() + 80.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.materials, key = { it.id }) { material ->
                    MaterialCard(
                        material = material,
                        onEdit = {
                            navController.navigate(
                                Screen.AddEditMaterial.route(projectId, material.phaseId, material.id)
                            )
                        },
                        onDelete = { deleteTarget = material }
                    )
                }
            }
        }
    }

    deleteTarget?.let { material ->
        DeleteConfirmDialog(
            itemName = "material",
            onConfirm = {
                vm.deleteMaterial(material.id)
                deleteTarget = null
            },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun MaterialCard(
    material: MaterialEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${material.quantity} ${material.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (material.unitCost > 0) {
                    Text(
                        text = "Unit: ${formatCurrency(material.unitCost)} • Total: ${formatCurrency(material.quantity * material.unitCost)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                StatusChip(material.status)
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, null, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            leadingIcon = { Icon(Icons.Default.Edit, null) },
                            onClick = { menuExpanded = false; onEdit() }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
                            onClick = { menuExpanded = false; onDelete() }
                        )
                    }
                }
            }
        }
    }
}
