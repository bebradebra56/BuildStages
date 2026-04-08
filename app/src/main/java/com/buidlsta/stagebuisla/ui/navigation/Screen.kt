package com.buidlsta.stagebuisla.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Projects : Screen("projects")
    object AllTasks : Screen("tasks_all")
    object Calendar : Screen("calendar")
    object More : Screen("more")

    object AddEditProject : Screen("project_edit?projectId={projectId}") {
        fun route(projectId: Long? = null) =
            if (projectId != null) "project_edit?projectId=$projectId" else "project_edit"
    }

    object Phases : Screen("phases/{projectId}") {
        fun route(projectId: Long) = "phases/$projectId"
    }

    object AddEditPhase : Screen("phase_edit/{projectId}?phaseId={phaseId}") {
        fun route(projectId: Long, phaseId: Long? = null) =
            if (phaseId != null) "phase_edit/$projectId?phaseId=$phaseId" else "phase_edit/$projectId"
    }

    object PhaseDetails : Screen("phase_details/{phaseId}") {
        fun route(phaseId: Long) = "phase_details/$phaseId"
    }

    object Tasks : Screen("tasks/{projectId}?phaseId={phaseId}") {
        fun route(projectId: Long, phaseId: Long? = null) =
            if (phaseId != null) "tasks/$projectId?phaseId=$phaseId" else "tasks/$projectId"
    }

    object AddEditTask : Screen("task_edit/{projectId}/{phaseId}?taskId={taskId}") {
        fun route(projectId: Long, phaseId: Long, taskId: Long? = null) =
            if (taskId != null) "task_edit/$projectId/$phaseId?taskId=$taskId"
            else "task_edit/$projectId/$phaseId"
    }

    object Materials : Screen("materials/{projectId}?phaseId={phaseId}") {
        fun route(projectId: Long, phaseId: Long? = null) =
            if (phaseId != null) "materials/$projectId?phaseId=$phaseId"
            else "materials/$projectId"
    }

    object AddEditMaterial : Screen("material_edit/{projectId}/{phaseId}?materialId={materialId}") {
        fun route(projectId: Long, phaseId: Long, materialId: Long? = null) =
            if (materialId != null) "material_edit/$projectId/$phaseId?materialId=$materialId"
            else "material_edit/$projectId/$phaseId"
    }

    object Timeline : Screen("timeline/{projectId}") {
        fun route(projectId: Long) = "timeline/$projectId"
    }

    object Photos : Screen("photos/{projectId}?phaseId={phaseId}") {
        fun route(projectId: Long, phaseId: Long? = null) =
            if (phaseId != null) "photos/$projectId?phaseId=$phaseId" else "photos/$projectId"
    }

    object AddPhoto : Screen("photo_add/{projectId}/{phaseId}") {
        fun route(projectId: Long, phaseId: Long) = "photo_add/$projectId/$phaseId"
    }

    object Budget : Screen("budget/{projectId}") {
        fun route(projectId: Long) = "budget/$projectId"
    }

    object AddEditExpense : Screen("expense_edit/{projectId}?expenseId={expenseId}") {
        fun route(projectId: Long, expenseId: Long? = null) =
            if (expenseId != null) "expense_edit/$projectId?expenseId=$expenseId"
            else "expense_edit/$projectId"
    }

    object Suppliers : Screen("suppliers")

    object AddEditSupplier : Screen("supplier_edit?supplierId={supplierId}") {
        fun route(supplierId: Long? = null) =
            if (supplierId != null) "supplier_edit?supplierId=$supplierId" else "supplier_edit"
    }

    object Equipment : Screen("equipment")

    object AddEditEquipment : Screen("equipment_edit?equipmentId={equipmentId}") {
        fun route(equipmentId: Long? = null) =
            if (equipmentId != null) "equipment_edit?equipmentId=$equipmentId"
            else "equipment_edit"
    }

    object Reports : Screen("reports/{projectId}") {
        fun route(projectId: Long) = "reports/$projectId"
    }

    object ActivityHistory : Screen("activity")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}
