package com.example.ui.screens.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CircularProgressIndicatorWithLabel
import com.example.ui.components.GlowCard
import com.example.ui.components.SubjectBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.JeeViewModel
import com.example.ui.viewmodel.TimerMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyTimerScreen(
    viewModel: JeeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()
    val todayMinutes by viewModel.todayMinutesStudied.collectAsStateWithLifecycle()
    val todaySessions by viewModel.todaySessions.collectAsStateWithLifecycle()
    val chapters by viewModel.chapters.collectAsStateWithLifecycle()

    var showChapterPicker by remember { mutableStateOf(false) }

    val progress = if (timerState.totalSeconds > 0) {
        timerState.remainingSeconds.toFloat() / timerState.totalSeconds
    } else 0f

    val minutes = timerState.remainingSeconds / 60
    val seconds = timerState.remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val timerColor = when (timerState.currentMode) {
        TimerMode.WORK -> ElectricBlue
        TimerMode.SHORT_BREAK -> AccentEmerald
        TimerMode.LONG_BREAK -> ElectricVioletLight
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "JEE Focus Pomodoro",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Deep Work & Retentive Learning",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
            )
        },
        containerColor = BgDark,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Timer Mode Switcher (Work, Short Break, Long Break)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimerMode.entries.forEach { mode ->
                    val isSelected = timerState.currentMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setTimerMode(mode) },
                        label = { Text(mode.title, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = timerColor.copy(alpha = 0.25f),
                            selectedLabelColor = timerColor
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subject and Chapter Selection
            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showChapterPicker = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SubjectBadge(subjectId = timerState.selectedSubject)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = timerState.selectedChapterName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Tap to change target chapter",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )
                        }
                    }

                    Icon(imageVector = Icons.Filled.ExpandMore, contentDescription = null, tint = TextTertiary)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Circular Timer Display
            CircularProgressIndicatorWithLabel(
                progress = progress,
                size = 230.dp,
                strokeWidth = 14.dp,
                color = timerColor,
                trackColor = SurfaceElevated
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 44.sp),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (timerState.isRunning) "FOCUSED STUDY" else "READY TO START",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (timerState.isRunning) timerColor else TextTertiary,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Control Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button
                IconButton(
                    onClick = { viewModel.resetTimer() },
                    modifier = Modifier
                        .size(48.dp)
                        .background(SurfaceElevated, CircleShape)
                ) {
                    Icon(imageVector = Icons.Filled.Replay, contentDescription = "Reset", tint = TextSecondary)
                }

                // Play / Pause FAB
                FloatingActionButton(
                    onClick = { viewModel.toggleTimer() },
                    containerColor = timerColor,
                    contentColor = TextPrimary,
                    modifier = Modifier
                        .size(64.dp)
                        .testTag("timer_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (timerState.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Toggle Timer",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Complete Early / Log
                IconButton(
                    onClick = { viewModel.completeTimerEarly() },
                    modifier = Modifier
                        .size(48.dp)
                        .background(SurfaceElevated, CircleShape)
                ) {
                    Icon(imageVector = Icons.Filled.DoneAll, contentDescription = "Complete Session", tint = AccentEmerald)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Today's Stats Bar
            Surface(
                color = SurfaceElevated,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Today's Focus Time", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                        Text(text = "${todayMinutes / 60}h ${todayMinutes % 60}m", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ElectricBlueLight)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Completed Sessions", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                        Text(text = "${todaySessions.count { it.isCompleted }}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AccentEmerald)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recent Sessions
            Text(
                text = "Today's Session Log",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(todaySessions) { session ->
                    Surface(
                        color = SurfaceCard,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = session.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text(text = "${session.durationMinutes} minutes • ${session.scheduledTime}", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                            }
                            if (session.isCompleted) {
                                Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showChapterPicker) {
        AlertDialog(
            onDismissRequest = { showChapterPicker = false },
            title = { Text("Select Target Chapter", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(chapters) { chap ->
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setTimerSubject(chap.subjectId, chap.name)
                                    showChapterPicker = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SubjectBadge(subjectId = chap.subjectId)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = chap.name, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showChapterPicker = false }) { Text("Close") }
            }
        )
    }
}
