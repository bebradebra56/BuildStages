@file:OptIn(ExperimentalMaterial3Api::class)
package com.buidlsta.stagebuisla.ui.screens.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.buidlsta.stagebuisla.ui.components.AppTopBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val vm: SettingsViewModel = koinViewModel()
    val state by vm.state.collectAsState()
    var themeExpanded by remember { mutableStateOf(false) }
    var currencyExpanded by remember { mutableStateOf(false) }

    val themes = listOf("System", "Light", "Dark")
    val currencies = listOf("$" to "USD - Dollar", "€" to "EUR - Euro", "£" to "GBP - Pound", "¥" to "JPY - Yen")

    Scaffold(
        topBar = { AppTopBar(title = "Settings", onBack = { navController.popBackStack() }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            SettingsSectionTitle("Appearance")
            Card(shape = RoundedCornerShape(16.dp)) {
                Column {
                    ExposedDropdownMenuBox(expanded = themeExpanded, onExpandedChange = { themeExpanded = it }) {
                        SettingsRow(
                            icon = Icons.Default.DarkMode,
                            title = "Theme",
                            subtitle = state.themeMode,
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        ) { themeExpanded = true }
                        ExposedDropdownMenu(expanded = themeExpanded, onDismissRequest = { themeExpanded = false }) {
                            themes.forEach { t ->
                                DropdownMenuItem(text = { Text(t) }, onClick = { vm.setThemeMode(t); themeExpanded = false })
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            SettingsSectionTitle("Notifications")
            Card(shape = RoundedCornerShape(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Enable Notifications", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text("Get reminders for deadlines", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = state.notificationsEnabled,
                        onCheckedChange = { vm.setNotificationsEnabled(it) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            SettingsSectionTitle("Financial")
            Card(shape = RoundedCornerShape(16.dp)) {
                ExposedDropdownMenuBox(expanded = currencyExpanded, onExpandedChange = { currencyExpanded = it }) {
                    SettingsRow(
                        icon = Icons.Default.AttachMoney,
                        title = "Currency",
                        subtitle = currencies.find { it.first == state.currencySymbol }?.second ?: "USD - Dollar",
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    ) { currencyExpanded = true }
                    ExposedDropdownMenu(expanded = currencyExpanded, onDismissRequest = { currencyExpanded = false }) {
                        currencies.forEach { (symbol, label) ->
                            DropdownMenuItem(
                                text = { Text("$symbol  $label") },
                                onClick = { vm.setCurrencySymbol(symbol); currencyExpanded = false }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            SettingsSectionTitle("About")
            Card(shape = RoundedCornerShape(16.dp)) {
                Column {
                    SettingsInfoRow(icon = Icons.Default.Info, title = "Version", value = "1.0.0")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsInfoRow(icon = Icons.Default.Business, title = "App", value = "BuildStages")
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun SettingsInfoRow(icon: ImageVector, title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
        Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
