package com.buidlsta.stagebuisla.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.buidlsta.stagebuisla.data.db.entity.TaskEntity
import com.buidlsta.stagebuisla.ui.components.*
import com.buidlsta.stagebuisla.ui.theme.StatusCompleted
import com.buidlsta.stagebuisla.ui.theme.StatusInProgress
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {
    val vm: CalendarViewModel = koinViewModel()
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(title = "Calendar")
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            )
        ) {
            item {
                MonthCalendar(
                    selectedDate = state.selectedDate,
                    taskDates = state.taskDates,
                    onDateSelected = { vm.selectDate(it) }
                )
            }

            item {
                val selectedDateStr = SimpleDateFormat("MMMM d, yyyy", Locale.US)
                    .format(Date(state.selectedDate))
                SectionHeader(title = "Tasks on $selectedDateStr")
            }

            if (state.isLoading) {
                item { LoadingScreen() }
                return@LazyColumn
            }

            if (state.tasksForDay.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.EventAvailable,
                                null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "No tasks for this day",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(state.tasksForDay, key = { it.id }) { task ->
                    CalendarTaskItem(
                        task = task,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthCalendar(
    selectedDate: Long,
    taskDates: Set<Long>,
    onDateSelected: (Long) -> Unit
) {
    val today = Calendar.getInstance()
    var displayMonth by remember { mutableStateOf(Calendar.getInstance()) }

    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
    val dayFormat = SimpleDateFormat("d", Locale.US)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    displayMonth = (displayMonth.clone() as Calendar).apply {
                        add(Calendar.MONTH, -1)
                    }
                }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous")
                }
                Text(
                    text = monthFormat.format(displayMonth.time),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = {
                    displayMonth = (displayMonth.clone() as Calendar).apply {
                        add(Calendar.MONTH, 1)
                    }
                }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next")
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            val cal = (displayMonth.clone() as Calendar).apply {
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
            val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val totalCells = firstDayOfWeek + daysInMonth
            val rows = (totalCells + 6) / 7

            repeat(rows) { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(7) { col ->
                        val cellIndex = row * 7 + col
                        val dayNum = cellIndex - firstDayOfWeek + 1
                        if (dayNum in 1..daysInMonth) {
                            val dayCal = (displayMonth.clone() as Calendar).apply {
                                set(Calendar.DAY_OF_MONTH, dayNum)
                                set(Calendar.HOUR_OF_DAY, 12)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            val dayMillis = dayCal.timeInMillis
                            val isToday = dayNum == today.get(Calendar.DAY_OF_MONTH) &&
                                    displayMonth.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                                    displayMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                            val isSelected = run {
                                val selCal = Calendar.getInstance().apply { timeInMillis = selectedDate }
                                selCal.get(Calendar.DAY_OF_MONTH) == dayNum &&
                                        selCal.get(Calendar.MONTH) == displayMonth.get(Calendar.MONTH) &&
                                        selCal.get(Calendar.YEAR) == displayMonth.get(Calendar.YEAR)
                            }
                            val hasTask = taskDates.any { taskDate ->
                                val taskCal = Calendar.getInstance().apply { timeInMillis = taskDate }
                                taskCal.get(Calendar.DAY_OF_MONTH) == dayNum &&
                                        taskCal.get(Calendar.MONTH) == displayMonth.get(Calendar.MONTH) &&
                                        taskCal.get(Calendar.YEAR) == displayMonth.get(Calendar.YEAR)
                            }

                            DayCell(
                                day = dayNum.toString(),
                                isToday = isToday,
                                isSelected = isSelected,
                                hasTask = hasTask,
                                onClick = { onDateSelected(dayMillis) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: String,
    isToday: Boolean,
    isSelected: Boolean,
    hasTask: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day,
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isToday -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (hasTask) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.primary
                        )
                )
            }
        }
    }
}

@Composable
private fun CalendarTaskItem(task: TaskEntity, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(8.dp).clip(CircleShape).background(
                    when (task.status) {
                        "Done" -> StatusCompleted
                        "InProgress" -> StatusInProgress
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(task.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                StatusChip(task.status)
            }
            PriorityChip(task.priority)
        }
    }
}
