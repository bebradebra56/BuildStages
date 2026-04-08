@file:OptIn(ExperimentalMaterial3Api::class)
package com.buidlsta.stagebuisla.ui.screens.materials

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
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditMaterialScreen(
    navController: NavController,
    projectId: Long,
    phaseId: Long,
    materialId: Long? = null
) {
    val vm: MaterialsViewModel = koinViewModel(parameters = { parametersOf(projectId) })
    val state by vm.state.collectAsState()
    val editMaterial by vm.editMaterial.collectAsState()
    val isEdit = materialId != null

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("pcs") }
    var unitCost by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Needed") }
    var selectedPhaseId by remember { mutableStateOf(phaseId) }
    var unitExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var phaseExpanded by remember { mutableStateOf(false) }

    val units = listOf("pcs", "kg", "ton", "m", "m²", "m³", "L", "bag", "roll", "box", "set")
    val statuses = listOf("Needed", "Ordered", "Delivered")
    val presets = listOf("Concrete", "Bricks", "Wood Planks", "Steel Rebar", "Sand", "Gravel", "Cement", "Tiles", "Drywall", "Paint")

    LaunchedEffect(materialId) {
        if (materialId != null) vm.loadEditMaterial(materialId)
    }

    LaunchedEffect(editMaterial) {
        editMaterial?.let { m ->
            name = m.name
            quantity = m.quantity.toString()
            unit = m.unit
            unitCost = if (m.unitCost > 0) m.unitCost.toString() else ""
            status = m.status
            selectedPhaseId = m.phaseId
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (isEdit) "Edit Material" else "Add Material",
                onBack = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isEdit) {
                Text("Quick select:", style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { preset ->
                        FilterChip(
                            selected = name == preset,
                            onClick = { name = preset },
                            label = { Text(preset) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Material name *") },
                leadingIcon = { Icon(Icons.Default.Inventory2, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Quantity *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false }
                    ) {
                        units.forEach { u ->
                            DropdownMenuItem(text = { Text(u) }, onClick = { unit = u; unitExpanded = false })
                        }
                    }
                }
            }

            OutlinedTextField(
                value = unitCost,
                onValueChange = { unitCost = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Unit cost") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                placeholder = { Text("0.00") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = it }
            ) {
                OutlinedTextField(
                    value = status,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    statuses.forEach { s ->
                        DropdownMenuItem(text = { Text(s) }, onClick = { status = s; statusExpanded = false })
                    }
                }
            }

            if (state.phases.isNotEmpty()) {
                ExposedDropdownMenuBox(
                    expanded = phaseExpanded,
                    onExpandedChange = { phaseExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.phases.find { it.id == selectedPhaseId }?.name ?: "Select phase",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Phase") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = phaseExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = phaseExpanded,
                        onDismissRequest = { phaseExpanded = false }
                    ) {
                        state.phases.forEach { phase ->
                            DropdownMenuItem(
                                text = { Text(phase.name) },
                                onClick = { selectedPhaseId = phase.id; phaseExpanded = false }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (name.isBlank() || quantity.isBlank()) return@Button
                    val qty = quantity.toDoubleOrNull() ?: return@Button
                    val cost = unitCost.toDoubleOrNull() ?: 0.0
                    if (isEdit && materialId != null) {
                        vm.updateMaterial(materialId, name, qty, unit, cost, status, selectedPhaseId)
                    } else {
                        vm.addMaterial(name, qty, unit, cost, status, selectedPhaseId)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = name.isNotBlank() && quantity.isNotBlank()
            ) {
                Icon(if (isEdit) Icons.Default.Save else Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text(if (isEdit) "Save Changes" else "Add Material")
            }
        }
    }
}
