package com.buidlsta.stagebuisla.di

import androidx.room.Room
import com.buidlsta.stagebuisla.data.db.AppDatabase
import com.buidlsta.stagebuisla.data.preferences.AppPreferences
import com.buidlsta.stagebuisla.data.repository.*
import com.buidlsta.stagebuisla.ui.screens.activity.ActivityViewModel
import com.buidlsta.stagebuisla.ui.screens.budget.BudgetViewModel
import com.buidlsta.stagebuisla.ui.screens.calendar.CalendarViewModel
import com.buidlsta.stagebuisla.ui.screens.dashboard.DashboardViewModel
import com.buidlsta.stagebuisla.ui.screens.equipment.EquipmentViewModel
import com.buidlsta.stagebuisla.ui.screens.materials.MaterialsViewModel
import com.buidlsta.stagebuisla.ui.screens.notifications.NotificationsViewModel
import com.buidlsta.stagebuisla.ui.screens.phases.PhaseDetailsViewModel
import com.buidlsta.stagebuisla.ui.screens.phases.PhasesViewModel
import com.buidlsta.stagebuisla.ui.screens.photos.PhotosViewModel
import com.buidlsta.stagebuisla.ui.screens.profile.ProfileViewModel
import com.buidlsta.stagebuisla.ui.screens.projects.ProjectsViewModel
import com.buidlsta.stagebuisla.ui.screens.reports.ReportsViewModel
import com.buidlsta.stagebuisla.ui.screens.settings.SettingsViewModel
import com.buidlsta.stagebuisla.ui.screens.suppliers.SuppliersViewModel
import com.buidlsta.stagebuisla.ui.screens.tasks.TasksViewModel
import com.buidlsta.stagebuisla.ui.screens.timeline.TimelineViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "buildstages.db"
        ).build()
    }
    single { get<AppDatabase>().projectDao() }
    single { get<AppDatabase>().phaseDao() }
    single { get<AppDatabase>().taskDao() }
    single { get<AppDatabase>().materialDao() }
    single { get<AppDatabase>().photoDao() }
    single { get<AppDatabase>().expenseDao() }
    single { get<AppDatabase>().supplierDao() }
    single { get<AppDatabase>().equipmentDao() }
    single { get<AppDatabase>().notificationDao() }
    single { get<AppDatabase>().activityLogDao() }
}

val preferencesModule = module {
    single { AppPreferences(androidContext()) }
}

val repositoryModule = module {
    single { ProjectRepository(get(), get()) }
    single { PhaseRepository(get(), get()) }
    single { TaskRepository(get(), get()) }
    single { MaterialRepository(get()) }
    single { PhotoRepository(get()) }
    single { ExpenseRepository(get()) }
    single { SupplierRepository(get()) }
    single { EquipmentRepository(get()) }
    single { NotificationRepository(get()) }
    single { ActivityLogRepository(get()) }
}

val viewModelModule = module {
    viewModel { DashboardViewModel(get(), get(), get(), get(), get()) }
    viewModel { (projectId: Long) -> ProjectsViewModel(get(), get(), projectId) }
    viewModel { (projectId: Long) -> PhasesViewModel(get(), get(), get(), projectId) }
    viewModel { (phaseId: Long) -> PhaseDetailsViewModel(get(), get(), get(), phaseId) }
    viewModel { (phaseId: Long, projectId: Long) -> TasksViewModel(get(), phaseId, projectId) }
    viewModel { (projectId: Long) -> MaterialsViewModel(get(), get(), projectId) }
    viewModel { (projectId: Long) -> PhotosViewModel(get(), get(), projectId) }
    viewModel { (projectId: Long) -> BudgetViewModel(get(), get(), projectId) }
    viewModel { SuppliersViewModel(get()) }
    viewModel { EquipmentViewModel(get()) }
    viewModel { (projectId: Long) -> ReportsViewModel(get(), get(), get(), get(), get(), projectId) }
    viewModel { (projectId: Long) -> TimelineViewModel(get(), get(), projectId) }
    viewModel { CalendarViewModel(get()) }
    viewModel { ActivityViewModel(get()) }
    viewModel { NotificationsViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}

val appModules = listOf(databaseModule, preferencesModule, repositoryModule, viewModelModule)
