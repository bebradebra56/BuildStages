package com.buidlsta.stagebuisla.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.buidlsta.stagebuisla.ui.screens.activity.ActivityHistoryScreen
import com.buidlsta.stagebuisla.ui.screens.budget.AddEditExpenseScreen
import com.buidlsta.stagebuisla.ui.screens.budget.BudgetScreen
import com.buidlsta.stagebuisla.ui.screens.calendar.CalendarScreen
import com.buidlsta.stagebuisla.ui.screens.dashboard.DashboardScreen
import com.buidlsta.stagebuisla.ui.screens.equipment.AddEditEquipmentScreen
import com.buidlsta.stagebuisla.ui.screens.equipment.EquipmentScreen
import com.buidlsta.stagebuisla.ui.screens.materials.AddEditMaterialScreen
import com.buidlsta.stagebuisla.ui.screens.materials.MaterialsScreen
import com.buidlsta.stagebuisla.ui.screens.more.MoreScreen
import com.buidlsta.stagebuisla.ui.screens.notifications.NotificationsScreen
import com.buidlsta.stagebuisla.ui.screens.onboarding.OnboardingScreen
import com.buidlsta.stagebuisla.ui.screens.phases.AddEditPhaseScreen
import com.buidlsta.stagebuisla.ui.screens.phases.PhaseDetailsScreen
import com.buidlsta.stagebuisla.ui.screens.phases.PhasesScreen
import com.buidlsta.stagebuisla.ui.screens.photos.AddPhotoScreen
import com.buidlsta.stagebuisla.ui.screens.photos.PhotosScreen
import com.buidlsta.stagebuisla.ui.screens.profile.ProfileScreen
import com.buidlsta.stagebuisla.ui.screens.projects.AddEditProjectScreen
import com.buidlsta.stagebuisla.ui.screens.projects.ProjectsScreen
import com.buidlsta.stagebuisla.ui.screens.reports.ReportsScreen
import com.buidlsta.stagebuisla.ui.screens.settings.SettingsScreen
import com.buidlsta.stagebuisla.ui.screens.splash.SplashScreen
import com.buidlsta.stagebuisla.ui.screens.suppliers.AddEditSupplierScreen
import com.buidlsta.stagebuisla.ui.screens.suppliers.SuppliersScreen
import com.buidlsta.stagebuisla.ui.screens.tasks.AddEditTaskScreen
import com.buidlsta.stagebuisla.ui.screens.tasks.TasksScreen
import com.buidlsta.stagebuisla.ui.screens.timeline.TimelineScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        composable(Screen.Projects.route) {
            ProjectsScreen(navController = navController)
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(navController = navController)
        }

        composable(
            route = Screen.AddEditProject.route,
            arguments = listOf(
                navArgument("projectId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStack ->
            val projectId = backStack.arguments?.getLong("projectId")
                ?.takeIf { it != -1L }
            AddEditProjectScreen(navController = navController, projectId = projectId)
        }

        composable(
            route = Screen.Phases.route,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) { backStack ->
            val projectId = backStack.arguments!!.getLong("projectId")
            PhasesScreen(navController = navController, projectId = projectId)
        }

        composable(
            route = Screen.AddEditPhase.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType },
                navArgument("phaseId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStack ->
            val projectId = backStack.arguments!!.getLong("projectId")
            val phaseId = backStack.arguments?.getLong("phaseId")?.takeIf { it != -1L }
            AddEditPhaseScreen(navController = navController, projectId = projectId, phaseId = phaseId)
        }

        composable(
            route = Screen.PhaseDetails.route,
            arguments = listOf(navArgument("phaseId") { type = NavType.LongType })
        ) { backStack ->
            val phaseId = backStack.arguments!!.getLong("phaseId")
            PhaseDetailsScreen(navController = navController, phaseId = phaseId)
        }

        composable(Screen.AllTasks.route) {
            TasksScreen(navController = navController, projectId = 0L, phaseId = 0L, showAll = true)
        }

        composable(
            route = Screen.Tasks.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType },
                navArgument("phaseId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStack ->
            val projectId = backStack.arguments!!.getLong("projectId")
            val phaseId = backStack.arguments?.getLong("phaseId") ?: 0L
            TasksScreen(navController = navController, projectId = projectId, phaseId = phaseId, showAll = false)
        }

        composable(
            route = Screen.AddEditTask.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType },
                navArgument("phaseId") { type = NavType.LongType },
                navArgument("taskId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStack ->
            val projectId = backStack.arguments!!.getLong("projectId")
            val phaseId = backStack.arguments!!.getLong("phaseId")
            val taskId = backStack.arguments?.getLong("taskId")?.takeIf { it != -1L }
            AddEditTaskScreen(
                navController = navController,
                projectId = projectId,
                phaseId = phaseId,
                taskId = taskId
            )
        }

        composable(
            route = Screen.Materials.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType },
                navArgument("phaseId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStack ->
            val projectId = backStack.arguments!!.getLong("projectId")
            val phaseId = backStack.arguments?.getLong("phaseId") ?: 0L
            MaterialsScreen(navController = navController, projectId = projectId, phaseId = phaseId)
        }

        composable(
            route = Screen.AddEditMaterial.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType },
                navArgument("phaseId") { type = NavType.LongType },
                navArgument("materialId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStack ->
            val projectId = backStack.arguments!!.getLong("projectId")
            val phaseId = backStack.arguments!!.getLong("phaseId")
            val materialId = backStack.arguments?.getLong("materialId")?.takeIf { it != -1L }
            AddEditMaterialScreen(
                navController = navController,
                projectId = projectId,
                phaseId = phaseId,
                materialId = materialId
            )
        }

        composable(
            route = Screen.Timeline.route,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) { backStack ->
            val projectId = backStack.arguments!!.getLong("projectId")
            TimelineScreen(navController = navController, projectId = projectId)
        }

        composable(
            route = Screen.Photos.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType },
                navArgument("phaseId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStack ->
            val projectId = backStack.arguments!!.getLong("projectId")
            val phaseId = backStack.arguments?.getLong("phaseId") ?: 0L
            PhotosScreen(navController = navController, projectId = projectId, phaseId = phaseId)
        }

        composable(
            route = Screen.AddPhoto.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType },
                navArgument("phaseId") { type = NavType.LongType }
            )
        ) { backStack ->
            val projectId = backStack.arguments!!.getLong("projectId")
            val phaseId = backStack.arguments!!.getLong("phaseId")
            AddPhotoScreen(navController = navController, projectId = projectId, phaseId = phaseId)
        }

        composable(
            route = Screen.Budget.route,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) { backStack ->
            val projectId = backStack.arguments!!.getLong("projectId")
            BudgetScreen(navController = navController, projectId = projectId)
        }

        composable(
            route = Screen.AddEditExpense.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType },
                navArgument("expenseId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStack ->
            val projectId = backStack.arguments!!.getLong("projectId")
            val expenseId = backStack.arguments?.getLong("expenseId")?.takeIf { it != -1L }
            AddEditExpenseScreen(navController = navController, projectId = projectId, expenseId = expenseId)
        }

        composable(Screen.Suppliers.route) {
            SuppliersScreen(navController = navController)
        }

        composable(
            route = Screen.AddEditSupplier.route,
            arguments = listOf(
                navArgument("supplierId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStack ->
            val supplierId = backStack.arguments?.getLong("supplierId")?.takeIf { it != -1L }
            AddEditSupplierScreen(navController = navController, supplierId = supplierId)
        }

        composable(Screen.Equipment.route) {
            EquipmentScreen(navController = navController)
        }

        composable(
            route = Screen.AddEditEquipment.route,
            arguments = listOf(
                navArgument("equipmentId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStack ->
            val equipmentId = backStack.arguments?.getLong("equipmentId")?.takeIf { it != -1L }
            AddEditEquipmentScreen(navController = navController, equipmentId = equipmentId)
        }

        composable(
            route = Screen.Reports.route,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) { backStack ->
            val projectId = backStack.arguments!!.getLong("projectId")
            ReportsScreen(navController = navController, projectId = projectId)
        }

        composable(Screen.ActivityHistory.route) {
            ActivityHistoryScreen(navController = navController)
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(Screen.More.route) {
            MoreScreen(navController = navController)
        }
    }
}
