package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.StudySessionEntity
import com.example.ui.components.*
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.JeeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: JeeViewModel,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val syllabusSummary by viewModel.syllabusSummary.collectAsStateWithLifecycle()
    val todaySessions by viewModel.todaySessions.collectAsStateWithLifecycle()
    val todayMinutes by viewModel.todayMinutesStudied.collectAsStateWithLifecycle()
    val activeBacklogs by viewModel.activeBacklogs.collectAsStateWithLifecycle()
    val mockTests by viewModel.mockTests.collectAsStateWithLifecycle()
    val streak by viewModel.currentStreak.collectAsStateWithLifecycle()

    val todayHours = todayMinutes / 60f
    val targetHours = userProfile?.dailyTargetHours ?: 6f
    val dailyProgress = (todayHours / targetHours).coerceIn(0f, 1f)

    val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    val formattedDate = dateFormat.format(Date())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Top Profile Banner (Name, Photo Avatar, and Exam Name) ---
        item {
            val user = userProfile
            val userName = user?.name ?: "Aspirant"
            val examName = user?.examName ?: "JEE Main 2027 & Advanced"
            val avatarType = user?.avatarType ?: "rocket"
            val (avatarIcon, avatarColor) = when (avatarType.lowercase()) {
                "atom" -> Pair(Icons.Filled.Science, ChemistryColor)
                "graduate" -> Pair(Icons.Filled.School, AccentGold)
                "trophy" -> Pair(Icons.Filled.EmojiEvents, AccentGold)
                "fire" -> Pair(Icons.Filled.LocalFireDepartment, AccentRose)
                "brain" -> Pair(Icons.Filled.Psychology, ElectricVioletLight)
                else -> Pair(Icons.Filled.RocketLaunch, ElectricBlueLight)
            }

            GlowCard(
                borderColor = ElectricBlue.copy(alpha = 0.5f),
                gradient = Brush.linearGradient(
                    colors = listOf(Color(0xFF131D36), SurfaceCard)
                ),
                onClick = { onNavigate(Screen.Profile) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_profile_top_banner")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Photo / Avatar with glowing badge
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(avatarColor.copy(alpha = 0.2f))
                            .border(2.dp, avatarColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = avatarIcon,
                            contentDescription = "Profile Photo",
                            tint = avatarColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hello, $userName! 👋",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            color = AccentGoldBg,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.35f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Flag,
                                    contentDescription = null,
                                    tint = AccentGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = examName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGold
                                )
                            }
                        }
                    }

                    // Streak Badge
                    Surface(
                        color = SurfaceElevated,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = AccentGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "$streak d",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold
                            )
                        }
                    }
                }
            }
        }

        // Exam Countdown Banner
        item {
            GlowCard(
                borderColor = ElectricBlue.copy(alpha = 0.4f),
                gradient = Brush.horizontalGradient(
                    colors = listOf(SurfaceCard, Color(0xFF162544))
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(ElectricBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Flag,
                                contentDescription = null,
                                tint = ElectricBlueLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "JEE Main 2027 Session 1",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = "Target: 99.5+ Percentile",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricBlueLight
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "72 Days",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                        Text(
                            text = "Remaining",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                    }
                }
            }
        }

        // --- 2. Daily Goal & Focus Time ---
        item {
            GlowCard(
                borderColor = SurfaceBorder,
                gradient = Brush.linearGradient(
                    colors = listOf(SurfaceCard, SurfaceElevated)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Study Target",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format(Locale.getDefault(), "%.1f", todayHours),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = " / ${targetHours.toInt()} hrs",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextTertiary,
                                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (todayHours >= targetHours) "Target achieved! Great consistency."
                            else "${String.format(Locale.getDefault(), "%.1f", targetHours - todayHours)} hrs left to complete daily goal.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (todayHours >= targetHours) AccentEmerald else TextSecondary
                        )
                    }

                    CircularProgressIndicatorWithLabel(
                        progress = dailyProgress,
                        size = 84.dp,
                        strokeWidth = 9.dp,
                        color = ElectricBlue,
                        trackColor = SurfaceDark
                    ) {
                        Text(
                            text = "${(dailyProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // --- 3. Quick Action Buttons ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Start Pomodoro
                FilledTonalButton(
                    onClick = { onNavigate(Screen.Timer) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ElectricBlue.copy(alpha = 0.2f),
                        contentColor = ElectricBlueLight
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_start_timer")
                ) {
                    Icon(imageVector = Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Focus Timer", style = MaterialTheme.typography.labelMedium)
                }

                // Plan Session
                FilledTonalButton(
                    onClick = { onNavigate(Screen.Planner) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ElectricViolet.copy(alpha = 0.2f),
                        contentColor = ElectricVioletLight
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_plan_session")
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Plan Study", style = MaterialTheme.typography.labelMedium)
                }

                // AI Coach
                FilledTonalButton(
                    onClick = { onNavigate(Screen.AiCoach) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = AccentGoldBg,
                        contentColor = AccentGold
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_ask_ai")
                ) {
                    Icon(imageVector = Icons.Filled.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "AI Coach", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // --- 4. Backlog Alert Banner (If Any Active) ---
        if (activeBacklogs.isNotEmpty()) {
            item {
                GlowCard(
                    borderColor = AccentRose.copy(alpha = 0.4f),
                    gradient = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF26131B), SurfaceCard)
                    ),
                    onClick = { onNavigate(Screen.Backlogs) },
                    modifier = Modifier.testTag("backlog_alert_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AccentRose.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = AccentRose,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${activeBacklogs.size} Pending Backlogs",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Top: ${activeBacklogs.first().chapterName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentRose
                                )
                            }
                        }

                        Button(
                            onClick = { onNavigate(Screen.Backlogs) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRose),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = "Recover", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }
                }
            }
        }

        // --- 5. Syllabus Progress Summary ---
        item {
            Column {
                SectionHeader(
                    title = "Syllabus Tracker",
                    actionLabel = "View All (${syllabusSummary.completedCount}/${syllabusSummary.totalCount})",
                    onActionClick = { onNavigate(Screen.Syllabus) }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Physics Card
                    SubjectProgressMiniCard(
                        subject = "Physics",
                        percentage = syllabusSummary.physicsPercentage,
                        color = PhysicsColor,
                        bg = PhysicsBg,
                        icon = Icons.Filled.Science,
                        modifier = Modifier.weight(1f)
                    )

                    // Chemistry Card
                    SubjectProgressMiniCard(
                        subject = "Chemistry",
                        percentage = syllabusSummary.chemistryPercentage,
                        color = ChemistryColor,
                        bg = ChemistryBg,
                        icon = Icons.Filled.Biotech,
                        modifier = Modifier.weight(1f)
                    )

                    // Mathematics Card
                    SubjectProgressMiniCard(
                        subject = "Maths",
                        percentage = syllabusSummary.mathsPercentage,
                        color = MathsColor,
                        bg = MathsBg,
                        icon = Icons.Filled.Calculate,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // --- 6. Today's Planned Sessions ---
        item {
            Column {
                SectionHeader(
                    title = "Today's Study Plan",
                    actionLabel = "Open Planner",
                    onActionClick = { onNavigate(Screen.Planner) }
                )

                if (todaySessions.isEmpty()) {
                    GlowCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = ElectricBlueLight,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No sessions planned for today yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            TextButton(onClick = { onNavigate(Screen.Planner) }) {
                                Text(text = "+ Schedule First Session", color = ElectricBlueLight)
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        todaySessions.forEach { session ->
                            TodaySessionRow(
                                session = session,
                                onToggle = { viewModel.toggleSessionComplete(session) }
                            )
                        }
                    }
                }
            }
        }

        // --- 7. Latest Mock Test Performance ---
        item {
            Column {
                SectionHeader(
                    title = "Recent Mock Test",
                    actionLabel = "Full Analysis",
                    onActionClick = { onNavigate(Screen.Analysis) }
                )

                val latestTest = mockTests.firstOrNull()
                if (latestTest != null) {
                    val scores = mockTests.map { it.marksObtained }.reversed()
                    GlowCard(
                        onClick = { onNavigate(Screen.Analysis) },
                        borderColor = SurfaceBorder
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = latestTest.testName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Date: ${latestTest.testDate} • ${latestTest.testType}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary
                                )
                            }

                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${latestTest.marksObtained}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (latestTest.marksObtained >= 180) AccentEmerald else AccentGold
                                )
                                Text(
                                    text = "/${latestTest.totalMarks}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextTertiary,
                                    modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TestStatPill("Physics", "${latestTest.physicsMarks}", PhysicsColor)
                            TestStatPill("Chemistry", "${latestTest.chemistryMarks}", ChemistryColor)
                            TestStatPill("Maths", "${latestTest.mathsMarks}", MathsColor)
                            TestStatPill(
                                "Accuracy",
                                "${(latestTest.correctQuestions.toFloat() / latestTest.attemptedQuestions.coerceAtLeast(1) * 100).toInt()}%",
                                ElectricBlueLight
                            )
                        }

                        if (scores.size > 1) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Score Trend:",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            PerformanceTrendMiniChart(
                                scores = scores,
                                maxScore = 300,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(45.dp)
                            )
                        }
                    }
                } else {
                    GlowCard {
                        Text(
                            text = "No mock tests recorded yet. Take a test and log your scores!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectProgressMiniCard(
    subject: String,
    percentage: Int,
    color: Color,
    bg: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(bg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subject,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { percentage / 100f },
                color = color,
                trackColor = SurfaceElevated,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
private fun TodaySessionRow(
    session: StudySessionEntity,
    onToggle: () -> Unit
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (session.isCompleted) AccentEmerald.copy(alpha = 0.4f) else SurfaceBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (session.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = "Toggle session",
                    tint = if (session.isCompleted) AccentEmerald else TextTertiary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (session.isCompleted) TextTertiary else TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SubjectBadge(subjectId = session.subjectId)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${session.durationMinutes}m • ${session.scheduledTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun TestStatPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}
