package com.example.ui.screens.backlogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.BacklogEntity
import com.example.domain.algorithm.BacklogPrioritizer
import com.example.domain.algorithm.RecoveryPlan
import com.example.ui.components.GlowCard
import com.example.ui.components.PriorityBadge
import com.example.ui.components.SubjectBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.JeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BacklogScreen(
    viewModel: JeeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allBacklogs by viewModel.allBacklogs.collectAsStateWithLifecycle()
    val chapters by viewModel.chapters.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var showPlanDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Active, 1 = Resolved

    val chaptersMap = remember(chapters) { chapters.associateBy { it.id } }
    val activeBacklogs = remember(allBacklogs) { allBacklogs.filter { !it.isResolved } }
    val resolvedBacklogs = remember(allBacklogs) { allBacklogs.filter { it.isResolved } }

    val displayedList = if (selectedTab == 0) activeBacklogs else resolvedBacklogs

    val recoveryPlan = remember(activeBacklogs, chaptersMap) {
        BacklogPrioritizer.generateRecoveryPlan(
            backlogs = activeBacklogs,
            chaptersMap = chaptersMap,
            availableExtraMinutesDaily = 75
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Backlog Recovery System",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Intelligent Catch-Up Engine",
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
                actions = {
                    FilledTonalButton(
                        onClick = { showPlanDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ElectricViolet.copy(alpha = 0.2f),
                            contentColor = ElectricVioletLight
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("smart_plan_button")
                    ) {
                        Icon(imageVector = Icons.Filled.AutoGraph, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Recovery Plan", style = MaterialTheme.typography.labelSmall)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ElectricBlue,
                contentColor = TextPrimary,
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("add_backlog_fab")
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Backlog")
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
            // Summary Card
            GlowCard(
                borderColor = if (activeBacklogs.isNotEmpty()) AccentRose.copy(alpha = 0.35f) else AccentEmerald.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pending Workload",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                        Text(
                            text = "${recoveryPlan.totalBacklogHours} Hours",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (activeBacklogs.isEmpty()) AccentEmerald else AccentRose
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Estimated Time to Clear",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                        Text(
                            text = "${recoveryPlan.estimatedDaysToClear} Days",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                        Text(
                            text = "at 75m extra/day",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Selector (Active vs Resolved)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceCard,
                contentColor = ElectricBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Active Backlogs (${activeBacklogs.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Recovered (${resolvedBacklogs.size})") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (displayedList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircleOutline,
                            contentDescription = null,
                            tint = AccentEmerald,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (selectedTab == 0) "No active backlogs! You're fully on track." else "No resolved backlogs recorded yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(displayedList) { backlog ->
                        BacklogCard(
                            backlog = backlog,
                            onToggleResolved = { viewModel.toggleBacklogResolved(backlog) },
                            onToggleLectures = { viewModel.updateBacklog(backlog.copy(lectureDone = !backlog.lectureDone)) },
                            onToggleQuestions = { viewModel.updateBacklog(backlog.copy(questionsDone = !backlog.questionsDone)) },
                            onToggleRevision = { viewModel.updateBacklog(backlog.copy(revisionDone = !backlog.revisionDone)) },
                            onDelete = { viewModel.deleteBacklog(backlog.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBacklogDialog(
            availableChapters = chapters,
            onDismiss = { showAddDialog = false },
            onAdd = { chapterId, chapterName, subjectId, hours, priority, understanding, targetDate, notes ->
                viewModel.addBacklog(chapterId, chapterName, subjectId, hours, priority, understanding, targetDate, notes)
                showAddDialog = false
            }
        )
    }

    if (showPlanDialog) {
        RecoveryPlanDialog(
            plan = recoveryPlan,
            onDismiss = { showPlanDialog = false }
        )
    }
}

@Composable
private fun BacklogCard(
    backlog: BacklogEntity,
    onToggleResolved: () -> Unit,
    onToggleLectures: () -> Unit,
    onToggleQuestions: () -> Unit,
    onToggleRevision: () -> Unit,
    onDelete: () -> Unit
) {
    GlowCard(
        borderColor = if (backlog.isResolved) AccentEmerald.copy(alpha = 0.3f) else SurfaceBorder,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SubjectBadge(subjectId = backlog.subjectId)
                    Spacer(modifier = Modifier.width(6.dp))
                    PriorityBadge(priority = backlog.priority)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Clarity: ${backlog.understandingLevel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = backlog.chapterName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (backlog.isResolved) TextTertiary else TextPrimary
                )
            }

            IconButton(
                onClick = onToggleResolved,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (backlog.isResolved) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = "Resolve Backlog",
                    tint = if (backlog.isResolved) AccentEmerald else TextTertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BacklogStepChip(label = "Lectures", isDone = backlog.lectureDone, onClick = onToggleLectures)
            BacklogStepChip(label = "DPP & Questions", isDone = backlog.questionsDone, onClick = onToggleQuestions)
            BacklogStepChip(label = "PYQ Revision", isDone = backlog.revisionDone, onClick = onToggleRevision)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Timer, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${backlog.estimatedHours}h est. workload",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(imageVector = Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = TextTertiary, modifier = Modifier.size(16.dp))
            }
        }

        if (backlog.notes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Notes: ${backlog.notes}",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun BacklogStepChip(label: String, isDone: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isDone) AccentEmeraldBg else SurfaceElevated,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDone) AccentEmerald.copy(alpha = 0.5f) else SurfaceBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = if (isDone) Icons.Filled.Check else Icons.Filled.Circle,
                contentDescription = null,
                tint = if (isDone) AccentEmerald else TextTertiary,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isDone) AccentEmerald else TextSecondary
            )
        }
    }
}

@Composable
fun RecoveryPlanDialog(
    plan: RecoveryPlan,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Automated Backlog Recovery Plan", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Allocating 75 minutes daily based on the 70-30 Golden Rule without overloading ongoing classes:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                if (plan.highYieldAlerts.isNotEmpty()) {
                    Surface(
                        color = AccentGoldBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            plan.highYieldAlerts.forEach { alert ->
                                Text(text = "• $alert", style = MaterialTheme.typography.labelSmall, color = AccentGold)
                            }
                        }
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(plan.dailySchedule) { slot ->
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = slot.dayLabel, style = MaterialTheme.typography.labelSmall, color = ElectricBlueLight)
                                    Text(text = slot.chapterName, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                                    Text(text = "Focus: ${slot.focusArea}", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                }
                                Text(text = "${slot.allocatedMinutes}m", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Got It")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBacklogDialog(
    availableChapters: List<com.example.data.model.ChapterEntity>,
    onDismiss: () -> Unit,
    onAdd: (chapterId: Long, chapterName: String, subjectId: String, hours: Int, priority: String, understanding: String, targetDate: String, notes: String) -> Unit
) {
    var selectedSubject by remember { mutableStateOf("physics") }
    val filtered = availableChapters.filter { it.subjectId == selectedSubject }

    var selectedChapter by remember { mutableStateOf(filtered.firstOrNull() ?: availableChapters.first()) }
    var estimatedHours by remember { mutableIntStateOf(8) }
    var priority by remember { mutableStateOf("High") }
    var understanding by remember { mutableStateOf("Low") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Pending Backlog", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Subject selection
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("physics" to "Physics", "chemistry" to "Chemistry", "mathematics" to "Maths").forEach { (id, name) ->
                        FilterChip(
                            selected = selectedSubject == id,
                            onClick = {
                                selectedSubject = id
                                val chap = availableChapters.firstOrNull { it.subjectId == id }
                                if (chap != null) selectedChapter = chap
                            },
                            label = { Text(name, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Chapter Picker
                OutlinedTextField(
                    value = selectedChapter.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selected Chapter") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Estimated Hours & Priority
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = estimatedHours.toString(),
                        onValueChange = { estimatedHours = it.toIntOrNull() ?: 6 },
                        label = { Text("Est. Hours") },
                        modifier = Modifier.weight(1f)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Priority", style = MaterialTheme.typography.labelSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Critical", "High", "Medium").forEach { p ->
                                FilterChip(
                                    selected = priority == p,
                                    onClick = { priority = p },
                                    label = { Text(p.take(4), style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }

                Text(text = "Current Understanding Level:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Low", "Medium", "Good").forEach { u ->
                        FilterChip(
                            selected = understanding == u,
                            onClick = { understanding = u },
                            label = { Text(u, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Why is this pending? (e.g. Missed coaching classes)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(selectedChapter.id, selectedChapter.name, selectedChapter.subjectId, estimatedHours, priority, understanding, "2026-11-20", notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Add Backlog")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
