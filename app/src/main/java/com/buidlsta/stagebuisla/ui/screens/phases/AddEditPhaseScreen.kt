@file:OptIn(ExperimentalMaterial3Api::class)
package com.buidlsta.stagebuisla.ui.screens.phases

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
import com.buidlsta.stagebuisla.ui.screens.projects.DatePickerDialog
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditPhaseScreen(
    navController: NavController,
    projectId: Long,
    phaseId: Long? = null
) {
    val vm: PhasesViewModel = koinViewModel(parameters = { parametersOf(projectId) })
    val editPhase by vm.editPhase.collectAsState()
    val isEdit = phaseId != null

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(0L) }
    var endDate by remember { mutableStateOf(0L) }
    var status by remember { mutableStateOf("Pending") }
    var progress by remember { mutableStateOf(0) }
    var statusExpanded by remember { mutableStateOf(false) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val statuses = listOf("Pending", "InProgress", "OnHold", "Completed")
    val presetPhases = listOf("Foundation", "Walls", "Roof", "Windows & Doors", "Electrical", "Plumbing", "Finishing", "Landscaping")

    LaunchedEffect(phaseId) {
        if (phaseId != null) {
            vm.loadEditPhase(phaseId)
        }
    }

    LaunchedEffect(editPhase) {
        editPhase?.let { p ->
            name = p.name
            description = p.description
            startDate = p.startDate
            endDate = p.endDate
            status = p.status
            progress = p.progress
        }
    }

    if (showStartPicker) {
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            onDateSelected = { startDate = it; showStartPicker = false }
        )
    }
    if (showEndPicker) {
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            onDateSelected = { endDate = it; showEndPicker = false }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (isEdit) "Edit Phase" else "Add Phase",
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
                    presetPhases.forEach { preset ->
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
                label = { Text("Phase name *") },
                leadingIcon = { Icon(Icons.Default.Layers, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                leadingIcon = { Icon(Icons.Default.Notes, null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = formatDateDisplay(startDate),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Start") },
                    trailingIcon = {
                        IconButton(onClick = { showStartPicker = true }) {
                            Icon(Icons.Default.CalendarToday, null)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = formatDateDisplay(endDate),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("End") },
                    trailingIcon = {
                        IconButton(onClick = { showEndPicker = true }) {
                            Icon(Icons.Default.CalendarToday, null)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            if (isEdit) {
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
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = { status = s; statusExpanded = false }
                            )
                        }
                    }
                }

                Column {
                    Text("Progress: $progress%", style = MaterialTheme.typography.labelLarge)
                    Slider(
                        value = progress.toFloat(),
                        onValueChange = { progress = it.toInt() },
                        valueRange = 0f..100f,
                        steps = 19
                    )
                }
            }

            Button(
                onClick = {
                    if (name.isBlank()) return@Button
                    if (isEdit && phaseId != null) {
                        vm.updatePhase(phaseId, name, description, startDate, endDate, status, progress)
                    } else {
                        vm.addPhase(name, description, startDate, endDate)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = name.isNotBlank()
            ) {
                Icon(if (isEdit) Icons.Default.Save else Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text(if (isEdit) "Save Changes" else "Add Phase")
            }
        }
    }
}

private fun formatDateDisplay(millis: Long): String {
    if (millis == 0L) return "Not set"
    return java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US).format(java.util.Date(millis))
}
