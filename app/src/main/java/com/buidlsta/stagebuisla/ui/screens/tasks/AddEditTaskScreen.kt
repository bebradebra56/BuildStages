@file:OptIn(ExperimentalMaterial3Api::class)
package com.buidlsta.stagebuisla.ui.screens.tasks

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    navController: NavController,
    projectId: Long,
    phaseId: Long,
    taskId: Long? = null
) {
    val vm: TasksViewModel = koinViewModel(parameters = { parametersOf(phaseId, projectId) })
    val editTask by vm.editTask.collectAsState()
    val isEdit = taskId != null

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf(0L) }
    var priority by remember { mutableStateOf("Medium") }
    var status by remember { mutableStateOf("Todo") }
    var priorityExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val priorities = listOf("Low", "Medium", "High")
    val statuses = listOf("Todo", "InProgress", "Done")

    LaunchedEffect(taskId) {
        if (taskId != null) vm.loadEditTask(taskId)
    }

    LaunchedEffect(editTask) {
        editTask?.let { t ->
            name = t.name
            description = t.description
            deadline = t.deadline
            priority = t.priority
            status = t.status
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { deadline = it; showDatePicker = false }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (isEdit) "Edit Task" else "Add Task",
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
                label = { Text("Task name *") },
                leadingIcon = { Icon(Icons.Default.Assignment, null) },
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

            OutlinedTextField(
                value = if (deadline == 0L) "No deadline" else formatDateDisplay(deadline),
                onValueChange = {},
                readOnly = true,
                label = { Text("Deadline") },
                trailingIcon = {
                    Row {
                        if (deadline != 0L) {
                            IconButton(onClick = { deadline = 0L }) {
                                Icon(Icons.Default.Clear, null)
                            }
                        }
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, null)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = priorityExpanded,
                onExpandedChange = { priorityExpanded = it }
            ) {
                OutlinedTextField(
                    value = priority,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    leadingIcon = { Icon(Icons.Default.PriorityHigh, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = priorityExpanded,
                    onDismissRequest = { priorityExpanded = false }
                ) {
                    priorities.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p) },
                            onClick = { priority = p; priorityExpanded = false }
                        )
                    }
                }
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

            Button(
                onClick = {
                    if (name.isBlank()) return@Button
                    if (isEdit && taskId != null) {
                        vm.updateTask(taskId, name, description, deadline, priority, status, phaseId, projectId)
                    } else {
                        vm.addTask(name, description, deadline, priority, phaseId, projectId)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = name.isNotBlank()
            ) {
                Icon(if (isEdit) Icons.Default.Save else Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text(if (isEdit) "Save Changes" else "Add Task")
            }
        }
    }
}

private fun formatDateDisplay(millis: Long): String {
    if (millis == 0L) return "No deadline"
    return java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US).format(java.util.Date(millis))
}
