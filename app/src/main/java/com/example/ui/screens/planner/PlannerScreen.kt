package com.example.ui.screens.planner

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.StudySessionEntity
import com.example.ui.components.GlowCard
import com.example.ui.components.SubjectBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.JeeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    viewModel: JeeViewModel,
    modifier: Modifier = Modifier
) {
    val allSessions by viewModel.sessions.collectAsStateWithLifecycle()
    val chapters by viewModel.chapters.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDayIndex by remember { mutableIntStateOf(0) } // 0 = Today, 1 = Tomorrow, etc.

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayFormat = SimpleDateFormat("EEE, d MMM", Locale.getDefault())

    val daysList = remember {
        val list = mutableListOf<Pair<String, String>>()
        val cal = Calendar.getInstance()
        for (i in 0 until 7) {
            val dateStr = sdf.format(cal.time)
            val label = if (i == 0) "Today" else displayFormat.format(cal.time)
            list.add(Pair(dateStr, label))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    val selectedDate = daysList.getOrNull(selectedDayIndex)?.first ?: viewModel.todayDate
    val filteredSessions = allSessions.filter { it.scheduledDate == selectedDate }

    // Check for overlapping sessions
    val hasOverlap = remember(filteredSessions) {
        if (filteredSessions.size < 2) false
        else {
            // Check times
            val sorted = filteredSessions.sortedBy { it.scheduledTime }
            var collision = false
            for (i in 0 until sorted.size - 1) {
                if (sorted[i].scheduledTime == sorted[i + 1].scheduledTime) {
                    collision = true
                    break
                }
            }
            collision
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ElectricBlue,
                contentColor = TextPrimary,
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("add_session_fab")
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Session")
            }
        },
        containerColor = BgDark,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Study Planner",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Smart Timetable & Scheduled Slots",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Surface(
                    color = SurfaceElevated,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Schedule, contentDescription = null, tint = ElectricBlueLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${filteredSessions.sumOf { it.durationMinutes }}m Total",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal Day Selector
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(daysList.size) { index ->
                    val (dateStr, label) = daysList[index]
                    val isSelected = index == selectedDayIndex
                    Surface(
                        color = if (isSelected) ElectricBlue else SurfaceCard,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) ElectricBlueLight else SurfaceBorder
                        ),
                        modifier = Modifier
                            .clickable { selectedDayIndex = index }
                            .testTag("day_selector_$index")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Overlap collision warning if detected
            if (hasOverlap) {
                Surface(
                    color = Color(0x33F59E0B),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.WarningAmber, contentDescription = null, tint = AccentGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Warning: Multiple sessions scheduled for the same time slot! Adjust times to avoid clash.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentGold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Sessions List
            if (filteredSessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.EventBusy,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No study sessions scheduled for this day.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap the + button below to plan a targeted session.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(filteredSessions) { session ->
                        PlannerSessionCard(
                            session = session,
                            onToggleComplete = { viewModel.toggleSessionComplete(session) },
                            onDelete = { viewModel.deleteSession(session.id) },
                            onRescheduleToTomorrow = {
                                val cal = Calendar.getInstance()
                                cal.add(Calendar.DAY_OF_YEAR, 1)
                                val tomorrow = sdf.format(cal.time)
                                viewModel.addSession(
                                    subjectId = session.subjectId,
                                    chapterName = session.chapterName,
                                    title = session.title,
                                    scheduledDate = tomorrow,
                                    scheduledTime = session.scheduledTime,
                                    durationMinutes = session.durationMinutes,
                                    sessionType = session.sessionType,
                                    notes = "Rescheduled: " + session.notes
                                )
                                viewModel.deleteSession(session.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddStudySessionDialog(
            defaultDate = selectedDate,
            availableChapters = chapters.map { it.name to it.subjectId },
            onDismiss = { showAddDialog = false },
            onAdd = { subject, chapter, title, date, time, duration, type, notes ->
                viewModel.addSession(
                    subjectId = subject,
                    chapterName = chapter,
                    title = title,
                    scheduledDate = date,
                    scheduledTime = time,
                    durationMinutes = duration,
                    sessionType = type,
                    notes = notes
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun PlannerSessionCard(
    session: StudySessionEntity,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onRescheduleToTomorrow: () -> Unit
) {
    GlowCard(
        borderColor = if (session.isCompleted) AccentEmerald.copy(alpha = 0.3f) else SurfaceBorder,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (session.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = "Toggle Complete",
                    tint = if (session.isCompleted) AccentEmerald else TextTertiary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SubjectBadge(subjectId = session.subjectId)

                    Surface(
                        color = SurfaceElevated,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = session.sessionType,
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlueLight,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (session.isCompleted) TextTertiary else TextPrimary
                )

                Text(
                    text = "Chapter: ${session.chapterName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.AccessTime, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${session.scheduledTime} (${session.durationMinutes} mins)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                    }

                    Row {
                        if (!session.isCompleted) {
                            TextButton(
                                onClick = onRescheduleToTomorrow,
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                            ) {
                                Text(text = "Push +1D", style = MaterialTheme.typography.labelSmall, color = AccentGold)
                            }
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = TextTertiary, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                if (session.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Notes: ${session.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudySessionDialog(
    defaultDate: String,
    availableChapters: List<Pair<String, String>>, // name to subjectId
    onDismiss: () -> Unit,
    onAdd: (subject: String, chapter: String, title: String, date: String, time: String, duration: Int, type: String, notes: String) -> Unit
) {
    var subject by remember { mutableStateOf("physics") }
    val filteredChapters = availableChapters.filter { it.second == subject }.map { it.first }

    var chapterName by remember { mutableStateOf(filteredChapters.firstOrNull() ?: "Kinematics 1D & 2D") }
    var title by remember { mutableStateOf("") }
    var scheduledDate by remember { mutableStateOf(defaultDate) }
    var scheduledTime by remember { mutableStateOf("10:00") }
    var durationMinutes by remember { mutableIntStateOf(90) }
    var sessionType by remember { mutableStateOf("DPP / Practice") }
    var notes by remember { mutableStateOf("") }

    val sessionTypes = listOf("Theory", "DPP / Practice", "PYQs", "Revision", "Mock Test")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Schedule Study Session", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Subject selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("physics" to "Physics", "chemistry" to "Chemistry", "mathematics" to "Maths").forEach { (id, name) ->
                        val isSelected = subject == id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                subject = id
                                val firstInSub = availableChapters.firstOrNull { it.second == id }?.first
                                if (firstInSub != null) chapterName = firstInSub
                            },
                            label = { Text(name, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Session Goal / Title") },
                    placeholder = { Text("e.g. Solve HC Verma ex 1 to 20") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Chapter name input / selector
                OutlinedTextField(
                    value = chapterName,
                    onValueChange = { chapterName = it },
                    label = { Text("Chapter") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Time and Duration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = scheduledTime,
                        onValueChange = { scheduledTime = it },
                        label = { Text("Time (HH:mm)") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = durationMinutes.toString(),
                        onValueChange = { durationMinutes = it.toIntOrNull() ?: 60 },
                        label = { Text("Minutes") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Session Type Chips
                Text(text = "Session Type:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(sessionTypes) { type ->
                        FilterChip(
                            selected = sessionType == type,
                            onClick = { sessionType = type },
                            label = { Text(type, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalTitle = if (title.isBlank()) "$sessionType: $chapterName" else title
                    onAdd(subject, chapterName, finalTitle, scheduledDate, scheduledTime, durationMinutes, sessionType, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
