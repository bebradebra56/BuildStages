@file:OptIn(ExperimentalMaterial3Api::class)
package com.buidlsta.stagebuisla.ui.screens.projects

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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProjectScreen(navController: NavController, projectId: Long? = null) {
    val vm: ProjectsViewModel = koinViewModel(parameters = { parametersOf(projectId ?: 0L) })
    val editProject by vm.editProject.collectAsState()

    val isEdit = projectId != null

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Residential") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf(0L) }
    var totalBudget by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("InProgress") }

    var typeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val projectTypes = listOf("Residential", "Commercial", "Industrial", "Renovation", "Infrastructure", "Other")
    val statuses = listOf("Planning", "InProgress", "OnHold", "Completed")

    LaunchedEffect(editProject) {
        editProject?.let { p ->
            name = p.name
            type = p.type
            description = p.description
            startDate = p.startDate
            endDate = p.endDate
            totalBudget = if (p.totalBudget > 0) p.totalBudget.toString() else ""
            status = p.status
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
                title = if (isEdit) "Edit Project" else "New Project",
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Project name *") },
                leadingIcon = { Icon(Icons.Default.Business, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it }
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Project type") },
                    leadingIcon = { Icon(Icons.Default.Category, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    projectTypes.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t) },
                            onClick = { type = t; typeExpanded = false }
                        )
                    }
                }
            }

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
                    label = { Text("Start date") },
                    trailingIcon = {
                        IconButton(onClick = { showStartPicker = true }) {
                            Icon(Icons.Default.CalendarToday, null)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = if (endDate == 0L) "Optional" else formatDateDisplay(endDate),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("End date") },
                    trailingIcon = {
                        IconButton(onClick = { showEndPicker = true }) {
                            Icon(Icons.Default.CalendarToday, null)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = totalBudget,
                onValueChange = { totalBudget = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Total budget") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                placeholder = { Text("0.00") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

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
                        leadingIcon = { Icon(Icons.Default.Flag, null) },
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
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (name.isBlank()) return@Button
                    val budget = totalBudget.toDoubleOrNull() ?: 0.0
                    if (isEdit && projectId != null) {
                        vm.updateProject(projectId, name, type, description, startDate, endDate, budget, status)
                    } else {
                        vm.addProject(name, type, description, startDate, endDate, budget)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = name.isNotBlank()
            ) {
                Icon(
                    if (isEdit) Icons.Default.Save else Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(if (isEdit) "Save Changes" else "Create Project")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let { onDateSelected(it) }
                }
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        }
    ) {
        DatePicker(state = state)
    }
}

private fun formatDateDisplay(millis: Long): String {
    if (millis == 0L) return "Not set"
    return SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(millis))
}
