package com.buidlsta.stagebuisla.ui.screens.more

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.buidlsta.stagebuisla.ui.navigation.Screen
import com.buidlsta.stagebuisla.ui.theme.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

data class MoreItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val route: String,
    val containerColor: Color
)

@Composable
fun MoreScreen(navController: NavController) {
    val items = listOf(
        MoreItem(Icons.Default.Store, "Suppliers", "Material suppliers", Screen.Suppliers.route, Blue40),
        MoreItem(Icons.Default.Construction, "Equipment", "Construction equipment", Screen.Equipment.route, Orange40),
        MoreItem(Icons.Default.History, "Activity", "Action history", Screen.ActivityHistory.route, Green40),
        MoreItem(Icons.Default.AccountCircle, "Profile", "Your information", Screen.Profile.route, Blue20),
        MoreItem(Icons.Default.Settings, "Settings", "App preferences", Screen.Settings.route, Grey50)
    )
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Surface(shadowElevation = 0.dp) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        text = "More",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "All features",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                MoreCard(item = item, onClick = { navController.navigate(item.route) })
            }
            item {
                MoreCard(
                    item = MoreItem(
                        Icons.Default.PrivacyTip, "Privacy Policy", "Tap to read", Screen.Suppliers.route, Blue40
                    )
                ) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://builldstages.com/privacy-policy.html"))
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
private fun MoreCard(item: MoreItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.containerColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    item.icon,
                    contentDescription = null,
                    tint = item.containerColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
