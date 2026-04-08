package com.buidlsta.stagebuisla.ui.screens.budget

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.buidlsta.stagebuisla.data.db.entity.ExpenseEntity
import com.buidlsta.stagebuisla.ui.components.*
import com.buidlsta.stagebuisla.ui.navigation.Screen
import com.buidlsta.stagebuisla.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(navController: NavController, projectId: Long) {
    val vm: BudgetViewModel = koinViewModel(parameters = { parametersOf(projectId) })
    val state by vm.state.collectAsState()
    var deleteTarget by remember { mutableStateOf<ExpenseEntity?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Budget",
                onBack = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddEditExpense.route(projectId)) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { innerPadding ->
        if (state.isLoading) {
            LoadingScreen()
            return@Scaffold
        }

        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 80.dp
            )
        ) {
            item {
                BudgetSummaryCard(state = state, modifier = Modifier.padding(16.dp))
            }

            if (state.spentByCategory.isNotEmpty()) {
                item {
                    CategoryPieChart(
                        spentByCategory = state.spentByCategory,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            item {
                SectionHeader(
                    title = "Expenses (${state.expenses.size})",
                    action = "Add",
                    onAction = { navController.navigate(Screen.AddEditExpense.route(projectId)) }
                )
            }

            if (state.expenses.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.Receipt,
                        title = "No expenses yet",
                        subtitle = "Add expenses to track your project budget"
                    )
                }
            } else {
                items(state.expenses, key = { it.id }) { expense ->
                    ExpenseItem(
                        expense = expense,
                        onEdit = { navController.navigate(Screen.AddEditExpense.route(projectId, expense.id)) },
                        onDelete = { deleteTarget = expense },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }

    deleteTarget?.let { expense ->
        DeleteConfirmDialog(
            itemName = "expense",
            onConfirm = {
                vm.deleteExpense(expense.id)
                deleteTarget = null
            },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun BudgetSummaryCard(state: BudgetState, modifier: Modifier = Modifier) {
    val budget = state.totalBudget
    val spent = state.totalSpent
    val progress = if (budget > 0) (spent / budget).coerceIn(0.0, 1.0) else 0.0
    val isOverBudget = spent > budget && budget > 0

    GradientCard(
        modifier = modifier.fillMaxWidth(),
        gradientColors = if (isOverBudget)
            listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer)
        else
            listOf(Orange40, Orange20)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Budget Overview",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = formatCurrency(budget),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text("Total Budget", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatCurrency(spent),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text("Spent", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                }
            }
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress.toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (isOverBudget) Color.White else Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "${(progress * 100).toInt()}% used",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = if (isOverBudget) "Over budget by ${formatCurrency(spent - budget)}"
                    else "Remaining: ${formatCurrency(state.remaining)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CategoryPieChart(spentByCategory: Map<String, Double>, modifier: Modifier = Modifier) {
    val total = spentByCategory.values.sum()
    val categoryColors = mapOf(
        "Labor" to CategoryLabor,
        "Materials" to CategoryMaterials,
        "Equipment" to CategoryEquipment,
        "Other" to CategoryOther
    )

    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("By Category", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    var startAngle = -90f
                    spentByCategory.forEach { (category, amount) ->
                        val sweepAngle = (amount / total * 360f).toFloat()
                        drawArc(
                            color = categoryColors[category] ?: CategoryOther,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = Offset(0f, 0f),
                            size = Size(size.width, size.height)
                        )
                        startAngle += sweepAngle
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    spentByCategory.forEach { (category, amount) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(categoryColors[category] ?: CategoryOther)
                            )
                            Spacer(Modifier.width(6.dp))
                            Column {
                                Text(category, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
                                Text(
                                    formatCurrency(amount),
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
}

@Composable
private fun ExpenseItem(
    expense: ExpenseEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val catColor = getCategoryColor(expense.category)

    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(catColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (expense.category) {
                        "Labor" -> Icons.Default.People
                        "Materials" -> Icons.Default.Inventory
                        "Equipment" -> Icons.Default.Construction
                        else -> Icons.Default.Receipt
                    },
                    null,
                    tint = catColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.description, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(expense.category, style = MaterialTheme.typography.labelSmall, color = catColor)
                    Text("•", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatDate(expense.date), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(
                text = formatCurrency(expense.amount),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Box {
                IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.MoreVert, null, modifier = Modifier.size(16.dp))
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
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
