package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.JeeDatabase
import com.example.data.repository.JeeRepository
import com.example.ui.navigation.BottomNavItems
import com.example.ui.navigation.Screen
import com.example.ui.screens.ai.AiCoachScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.backlogs.BacklogScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.mocktests.MockTestScreen
import com.example.ui.screens.planner.PlannerScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.syllabus.SyllabusScreen
import com.example.ui.screens.timer.StudyTimerScreen
import com.example.ui.screens.tutorial.TutorialScreen
import com.example.ui.theme.BgDark
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.JeeCompanionTheme
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.JeeViewModel
import com.example.ui.viewmodel.JeeViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = JeeDatabase.getInstance(applicationContext)
        val repository = JeeRepository(database.jeeDao())

        setContent {
            JeeCompanionTheme {
                val viewModel: JeeViewModel = viewModel(
                    factory = JeeViewModelFactory(repository)
                )

                val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

                // Determine whether user needs Auth or Tutorial
                val isLoggedIn = userProfile?.isLoggedIn == true
                val isTutorialCompleted = userProfile?.isTutorialCompleted == true

                val activeDestination = when {
                    !isLoggedIn -> Screen.Auth
                    !isTutorialCompleted -> Screen.Tutorial
                    else -> currentScreen
                }

                // Handle device back button
                BackHandler(enabled = activeDestination != Screen.Home && activeDestination != Screen.Auth) {
                    currentScreen = Screen.Home
                }

                val showBottomBar = activeDestination in BottomNavItems

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BgDark),
                    containerColor = BgDark,
                    contentWindowInsets = WindowInsets.safeDrawing,
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = SurfaceCard,
                                contentColor = TextSecondary,
                                windowInsets = WindowInsets.navigationBars,
                                tonalElevation = 8.dp,
                                modifier = Modifier.testTag("main_bottom_nav")
                            ) {
                                BottomNavItems.forEach { screen ->
                                    val isSelected = activeDestination == screen
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { currentScreen = screen },
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                                contentDescription = screen.title
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = screen.title,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = ElectricBlueLight,
                                            selectedTextColor = ElectricBlueLight,
                                            indicatorColor = ElectricBlue.copy(alpha = 0.2f),
                                            unselectedIconColor = TextSecondary,
                                            unselectedTextColor = TextSecondary
                                        ),
                                        modifier = Modifier.testTag("nav_${screen.route}")
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (activeDestination) {
                            Screen.Auth -> AuthScreen(
                                viewModel = viewModel,
                                onAuthSuccess = {
                                    currentScreen = Screen.Tutorial
                                }
                            )

                            Screen.Tutorial -> TutorialScreen(
                                viewModel = viewModel,
                                onFinishTutorial = {
                                    currentScreen = Screen.Home
                                }
                            )

                            Screen.Home -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigate = { destination -> currentScreen = destination }
                            )

                            Screen.Planner -> PlannerScreen(
                                viewModel = viewModel
                            )

                            Screen.Syllabus -> SyllabusScreen(
                                viewModel = viewModel
                            )

                            Screen.Analysis -> MockTestScreen(
                                viewModel = viewModel
                            )

                            Screen.AiCoach -> AiCoachScreen(
                                viewModel = viewModel
                            )

                            Screen.Timer -> StudyTimerScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = Screen.Home }
                            )

                            Screen.Backlogs -> BacklogScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = Screen.Home }
                            )

                            Screen.Profile -> ProfileScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = Screen.Home },
                                onRevisitTutorial = { currentScreen = Screen.Tutorial },
                                onLogout = {
                                    viewModel.logout()
                                    currentScreen = Screen.Auth
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
