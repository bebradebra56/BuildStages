@file:OptIn(ExperimentalMaterial3Api::class)
package com.buidlsta.stagebuisla.ui.screens.equipment

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.buidlsta.stagebuisla.ui.components.AppTopBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditEquipmentScreen(navController: NavController, equipmentId: Long? = null) {
    val vm: EquipmentViewModel = koinViewModel()
    val editEquipment by vm.editEquipment.collectAsState()
    val isEdit = equipmentId != null

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Available") }
    var statusExpanded by remember { mutableStateOf(false) }

    val statuses = listOf("Available", "InUse", "Maintenance")
    val presets = listOf("Crane", "Concrete Mixer", "Excavator", "Forklift", "Scaffolding", "Drill", "Compressor", "Generator")

    LaunchedEffect(equipmentId) { if (equipmentId != null) vm.loadEditEquipment(equipmentId) }
    LaunchedEffect(editEquipment) {
        editEquipment?.let { e -> name = e.name; type = e.type; status = e.status }
    }

    Scaffold(topBar = { AppTopBar(title = if (isEdit) "Edit Equipment" else "Add Equipment", onBack = { navController.popBackStack() }) }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isEdit) {
                Text("Quick select:", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    presets.forEach { preset ->
                        FilterChip(selected = name == preset, onClick = { name = preset }, label = { Text(preset) })
                    }
                }
            }
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Equipment name *") }, leadingIcon = { Icon(Icons.Default.Construction, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type") }, leadingIcon = { Icon(Icons.Default.Category, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = it }) {
                OutlinedTextField(value = status, onValueChange = {}, readOnly = true, label = { Text("Status") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                    statuses.forEach { s -> DropdownMenuItem(text = { Text(s) }, onClick = { status = s; statusExpanded = false }) }
                }
            }
            Button(
                onClick = {
                    if (name.isBlank()) return@Button
                    if (isEdit && equipmentId != null) vm.updateEquipment(equipmentId, name, type, status)
                    else vm.addEquipment(name, type, status)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), enabled = name.isNotBlank()
            ) {
                Icon(if (isEdit) Icons.Default.Save else Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text(if (isEdit) "Save Changes" else "Add Equipment")
            }
        }
    }
}
