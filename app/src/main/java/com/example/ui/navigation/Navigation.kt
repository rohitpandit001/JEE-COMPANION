package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : Screen(
        route = "dashboard",
        title = "Home",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    )

    data object Planner : Screen(
        route = "planner",
        title = "Planner",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    )

    data object Syllabus : Screen(
        route = "syllabus",
        title = "Syllabus",
        selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
        unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook
    )

    data object Analysis : Screen(
        route = "analysis",
        title = "Analysis",
        selectedIcon = Icons.Filled.Insights,
        unselectedIcon = Icons.Outlined.Insights
    )

    data object AiCoach : Screen(
        route = "ai_coach",
        title = "AI Coach",
        selectedIcon = Icons.Filled.Psychology,
        unselectedIcon = Icons.Outlined.Psychology
    )

    // Secondary sub-screens
    data object Timer : Screen(
        route = "timer",
        title = "Study Timer",
        selectedIcon = Icons.Filled.Timer,
        unselectedIcon = Icons.Outlined.Timer
    )

    data object Backlogs : Screen(
        route = "backlogs",
        title = "Backlog Recovery",
        selectedIcon = Icons.Filled.ReportProblem,
        unselectedIcon = Icons.Outlined.ReportProblem
    )

    data object Profile : Screen(
        route = "profile",
        title = "Profile & Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    data object Auth : Screen(
        route = "auth",
        title = "Authentication",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    data object Tutorial : Screen(
        route = "tutorial",
        title = "Tutorial Tour",
        selectedIcon = Icons.Filled.School,
        unselectedIcon = Icons.Outlined.School
    )
}

val BottomNavItems = listOf(
    Screen.Home,
    Screen.Planner,
    Screen.Syllabus,
    Screen.Analysis,
    Screen.AiCoach
)
