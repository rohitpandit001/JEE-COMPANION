package com.example.ui.screens.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlowCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.JeeViewModel

data class TutorialStep(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val tip: String
)

val tutorialSteps = listOf(
    TutorialStep(
        title = "Personalized JEE Dashboard",
        description = "Track your daily study target hours, consecutive study streaks, upcoming sessions, and countdown to JEE Main & Advanced.",
        icon = Icons.Filled.Dashboard,
        accentColor = ElectricBlueLight,
        tip = "Consistency is king: 6 hours daily beats 12 hours once a week!"
    ),
    TutorialStep(
        title = "Interactive Syllabus Tracker",
        description = "Class 11 & 12 syllabus across Physics, Chemistry, and Mathematics with separate lecture %, DPP solving, and PYQ progress tracking.",
        icon = Icons.AutoMirrored.Filled.MenuBook,
        accentColor = ChemistryColor,
        tip = "Filter by '🔥 High Yield' to focus on high-weightage chapters first."
    ),
    TutorialStep(
        title = "Smart Backlog Recovery",
        description = "Never drop current classes! The 70-30 rule automatically schedules 75m daily recovery sessions prioritizing low understanding topics.",
        icon = Icons.Filled.ReportProblem,
        accentColor = AccentRose,
        tip = "Critical backlogs are highlighted with recommended action drills."
    ),
    TutorialStep(
        title = "Mock Test & Mistake Notebook",
        description = "Diagnose marks loss! Categorize errors into Conceptual, Calculation, Interpretation, and Silly mistakes to prevent repetition.",
        icon = Icons.Filled.Insights,
        accentColor = AccentGold,
        tip = "Re-solving errors within 24 hours boosts retention by 80%."
    ),
    TutorialStep(
        title = "Pomodoro Timer & AI Coach",
        description = "Deep study intervals with automatic database logging, plus instant diagnostic advice from your personal AI Study Coach.",
        icon = Icons.Filled.Psychology,
        accentColor = ElectricVioletLight,
        tip = "Ask AI Coach to analyze your weak chapters or plan your week!"
    )
)

@Composable
fun TutorialScreen(
    viewModel: JeeViewModel,
    onFinishTutorial: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val step = tutorialSteps[currentStep]
    val isLast = currentStep == tutorialSteps.size - 1

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Skip Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "App Tour & Guide (${currentStep + 1}/${tutorialSteps.size})",
                style = MaterialTheme.typography.labelMedium,
                color = TextTertiary
            )
            TextButton(
                onClick = {
                    viewModel.completeTutorial()
                    onFinishTutorial()
                }
            ) {
                Text(text = "Skip Tour", color = ElectricBlueLight, fontWeight = FontWeight.SemiBold)
            }
        }

        // Center Content Card
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(step.accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = step.icon,
                    contentDescription = null,
                    tint = step.accentColor,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = step.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = step.description,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            GlowCard(
                borderColor = step.accentColor.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Lightbulb,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pro Tip: ${step.tip}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Bottom Controls: Dots Indicator & Next / Start Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tutorialSteps.indices.forEach { index ->
                    val isSelected = index == currentStep
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 10.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) ElectricBlueLight else SurfaceElevated)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (isLast) {
                        viewModel.completeTutorial()
                        onFinishTutorial()
                    } else {
                        currentStep++
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("tutorial_next_button")
            ) {
                Text(
                    text = if (isLast) "Start My JEE Journey 🚀" else "Next Feature",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
