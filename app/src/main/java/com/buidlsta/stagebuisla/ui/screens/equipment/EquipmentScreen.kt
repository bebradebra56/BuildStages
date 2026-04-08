package com.buidlsta.stagebuisla.ui.screens.equipment

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.buidlsta.stagebuisla.data.db.entity.EquipmentEntity
import com.buidlsta.stagebuisla.ui.components.*
import com.buidlsta.stagebuisla.ui.navigation.Screen
import com.buidlsta.stagebuisla.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun EquipmentScreen(navController: NavController) {
    val vm: EquipmentViewModel = koinViewModel()
    val state by vm.state.collectAsState()
    var deleteTarget by remember { mutableStateOf<EquipmentEntity?>(null) }

    Scaffold(
        topBar = { AppTopBar(title = "Equipment", onBack = { navController.popBackStack() }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddEditEquipment.route()) }) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { innerPadding ->
        if (state.isLoading) { LoadingScreen(); return@Scaffold }
        if (state.equipment.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.Construction, "No equipment yet", "Add construction equipment like cranes, mixers...")
            }
            return@Scaffold
        }
        LazyColumn(
            contentPadding = PaddingValues(top = innerPadding.calculateTopPadding() + 8.dp, bottom = innerPadding.calculateBottomPadding() + 80.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.equipment, key = { it.id }) { eq ->
                EquipmentCard(
                    equipment = eq,
                    onEdit = { navController.navigate(Screen.AddEditEquipment.route(eq.id)) },
                    onDelete = { deleteTarget = eq }
                )
            }
        }
    }

    deleteTarget?.let { eq ->
        DeleteConfirmDialog("equipment", onConfirm = { vm.deleteEquipment(eq.id); deleteTarget = null }, onDismiss = { deleteTarget = null })
    }
}

@Composable
private fun EquipmentCard(equipment: EquipmentEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }
    val statusColor = when (equipment.status) {
        "Available" -> StatusCompleted
        "InUse" -> StatusInProgress
        "Maintenance" -> StatusOnHold
        else -> StatusPending
    }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Construction, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(equipment.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                if (equipment.type.isNotEmpty()) {
                    Text(equipment.type, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            StatusChip(equipment.status)
            Spacer(Modifier.width(4.dp))
            Box {
                IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.MoreVert, null, modifier = Modifier.size(16.dp))
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(text = { Text("Edit") }, leadingIcon = { Icon(Icons.Default.Edit, null) }, onClick = { menuExpanded = false; onEdit() })
                    DropdownMenuItem(text = { Text("Delete", color = MaterialTheme.colorScheme.error) }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }, onClick = { menuExpanded = false; onDelete() })
                }
            }
        }
    }
}
