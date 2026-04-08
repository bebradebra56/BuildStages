package com.buidlsta.stagebuisla.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Default.Home, Screen.Dashboard.route),
    BottomNavItem("Projects", Icons.Default.Business, Screen.Projects.route),
    BottomNavItem("Tasks", Icons.Default.CheckCircle, Screen.AllTasks.route),
    BottomNavItem("Calendar", Icons.Default.CalendarMonth, Screen.Calendar.route),
    BottomNavItem("More", Icons.Default.GridView, Screen.More.route)
)

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

fun isBottomNavRoute(route: String?): Boolean {
    if (route == null) return false
    val bottomRoutes = setOf(
        Screen.Dashboard.route,
        Screen.Projects.route,
        Screen.AllTasks.route,
        Screen.Calendar.route,
        Screen.More.route
    )
    return bottomRoutes.contains(route)
}
