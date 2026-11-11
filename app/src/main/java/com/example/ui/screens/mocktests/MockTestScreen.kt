package com.example.ui.screens.mocktests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.model.MistakeEntity
import com.example.data.model.MockTestEntity
import com.example.domain.algorithm.MockTestAnalytics
import com.example.ui.components.GlowCard
import com.example.ui.components.PerformanceTrendMiniChart
import com.example.ui.components.SubjectBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.JeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockTestScreen(
    viewModel: JeeViewModel,
    modifier: Modifier = Modifier
) {
    val mockTests by viewModel.mockTests.collectAsStateWithLifecycle()
    val mistakes by viewModel.mistakes.collectAsStateWithLifecycle()
    val chapters by viewModel.chapters.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Test History, 1 = Mistake Notebook
    var showAddTestDialog by remember { mutableStateOf(false) }
    var showAddMistakeDialog by remember { mutableStateOf(false) }
    var selectedMistakeCategory by remember { mutableStateOf("All") }

    val mistakeAnalysis = remember(mistakes) {
        MockTestAnalytics.analyzeMistakes(mistakes)
    }

    val filteredMistakes = remember(mistakes, selectedMistakeCategory) {
        if (selectedMistakeCategory == "All") mistakes
        else mistakes.filter { it.mistakeType.equals(selectedMistakeCategory, ignoreCase = true) }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showAddTestDialog = true
                    else showAddMistakeDialog = true
                },
                containerColor = ElectricBlue,
                contentColor = TextPrimary,
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("add_test_or_mistake_fab")
            ) {
                Icon(
                    imageVector = if (selectedTab == 0) Icons.Filled.Addchart else Icons.Filled.PostAdd,
                    contentDescription = "Add Item"
                )
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
                        text = "Mock Test Analyzer",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Exam Diagnostics & Mistake Notebook",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceCard,
                contentColor = ElectricBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Mock Tests (${mockTests.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Mistake Notebook (${mistakes.size})") }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedTab == 0) {
                // Mock Tests Tab
                val scores = mockTests.map { it.marksObtained }.reversed()
                if (scores.size > 1) {
                    GlowCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Historical Score Trend (Out of 300)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PerformanceTrendMiniChart(
                            scores = scores,
                            maxScore = 300,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (mockTests.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No mock tests recorded yet. Tap + to add your first test score!", color = TextSecondary)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        items(mockTests) { test ->
                            MockTestDetailCard(
                                test = test,
                                onDelete = { viewModel.deleteMockTest(test) }
                            )
                        }
                    }
                }
            } else {
                // Mistake Notebook Tab
                // Repeat mistake alert
                if (mistakeAnalysis.repeatMistakeChapters.isNotEmpty()) {
                    Surface(
                        color = AccentRoseBg,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentRose.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Warning, contentDescription = null, tint = AccentRose, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "High Repeat Mistake Chapters:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentRose
                                )
                                Text(
                                    text = mistakeAnalysis.repeatMistakeChapters.joinToString(", "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Filter chips by category
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val categories = listOf("All", "Conceptual", "Calculation", "Interpretation", "Time Management", "Silly")
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedMistakeCategory == cat,
                            onClick = { selectedMistakeCategory = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (filteredMistakes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No mistakes found in this category.", color = TextSecondary)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        items(filteredMistakes) { mistake ->
                            MistakeCard(
                                mistake = mistake,
                                onDelete = { viewModel.deleteMistake(mistake) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddTestDialog) {
        AddMockTestDialog(
            onDismiss = { showAddTestDialog = false },
            onAdd = { name, date, total, obt, p, c, m, att, cor, incor, un, type, notes ->
                viewModel.addMockTest(name, date, total, obt, p, c, m, att, cor, incor, un, type, notes)
                showAddTestDialog = false
            }
        )
    }

    if (showAddMistakeDialog) {
        AddMistakeDialog(
            availableTests = mockTests,
            availableChapters = chapters.map { it.name },
            onDismiss = { showAddMistakeDialog = false },
            onAdd = { testId, testName, subjectId, chapter, qNum, type, desc, notes ->
                viewModel.addMistake(testId, testName, subjectId, chapter, qNum, type, desc, notes)
                showAddMistakeDialog = false
            }
        )
    }
}

@Composable
private fun MockTestDetailCard(
    test: MockTestEntity,
    onDelete: () -> Unit
) {
    val summary = remember(test) { MockTestAnalytics.computeTestSummary(test) }

    GlowCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = test.testName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${test.testDate} • ${test.testType}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${test.marksObtained}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (test.marksObtained >= 180) AccentEmerald else AccentGold
                )
                Text(
                    text = "/${test.totalMarks}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary,
                    modifier = Modifier.padding(start = 2.dp, top = 8.dp)
                )

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = TextTertiary, modifier = Modifier.size(18.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Subject Breakdown Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScoreSubjectBox("Physics", "${test.physicsMarks}/100", PhysicsColor, Modifier.weight(1f))
            ScoreSubjectBox("Chemistry", "${test.chemistryMarks}/100", ChemistryColor, Modifier.weight(1f))
            ScoreSubjectBox("Maths", "${test.mathsMarks}/100", MathsColor, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Analytics Row: Accuracy, Attempts, Negative Marks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Accuracy: ", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                Text(text = "${summary.accuracyPct.toInt()}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = ElectricBlueLight)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Attempts: ", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                Text(text = "${test.attemptedQuestions}/75", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Negative Marks: ", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                Text(text = "-${summary.negativeMarksLost}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = AccentRose)
            }
        }

        if (test.notes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Post-Test Review: ${test.notes}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ScoreSubjectBox(subject: String, score: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = SurfaceElevated,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = subject, style = MaterialTheme.typography.labelSmall, color = color)
            Text(text = score, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
private fun MistakeCard(
    mistake: MistakeEntity,
    onDelete: () -> Unit
) {
    val (color, bg) = when (mistake.mistakeType.lowercase()) {
        "conceptual" -> Pair(AccentRose, AccentRoseBg)
        "calculation" -> Pair(AccentOrange, Color(0x26FB923C))
        "interpretation" -> Pair(AccentGold, AccentGoldBg)
        "time management" -> Pair(ElectricVioletLight, Color(0x268B5CF6))
        else -> Pair(ElectricBlueLight, Color(0x263B82F6))
    }

    GlowCard(
        borderColor = color.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SubjectBadge(subjectId = mistake.subjectId)
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    color = bg,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = mistake.mistakeType.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(imageVector = Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = TextTertiary, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "${mistake.chapterName} • Q#${mistake.questionNumber}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Error: ${mistake.description}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        if (mistake.correctionNotes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = SurfaceElevated,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "Key Takeaway / Rule:", style = MaterialTheme.typography.labelSmall, color = AccentEmerald, fontWeight = FontWeight.Bold)
                    Text(text = mistake.correctionNotes, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun AddMockTestDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, date: String, total: Int, obt: Int, p: Int, c: Int, m: Int, att: Int, cor: Int, incor: Int, un: Int, type: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026-11-10") }
    var physicsMarks by remember { mutableIntStateOf(65) }
    var chemMarks by remember { mutableIntStateOf(70) }
    var mathsMarks by remember { mutableIntStateOf(55) }
    var correctQ by remember { mutableIntStateOf(50) }
    var incorrectQ by remember { mutableIntStateOf(8) }
    var notes by remember { mutableStateOf("") }

    val totalMarks = 300
    val totalObtained = physicsMarks + chemMarks + mathsMarks
    val attempted = correctQ + incorrectQ
    val unattempted = 75 - attempted

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Mock Test Result", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Test Name / Series") },
                    placeholder = { Text("e.g. Allen Major Test 1") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Subject Scores
                Text(text = "Subject Marks (out of 100):", style = MaterialTheme.typography.labelSmall)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = physicsMarks.toString(),
                        onValueChange = { physicsMarks = it.toIntOrNull() ?: 0 },
                        label = { Text("Physics") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = chemMarks.toString(),
                        onValueChange = { chemMarks = it.toIntOrNull() ?: 0 },
                        label = { Text("Chemistry") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = mathsMarks.toString(),
                        onValueChange = { mathsMarks = it.toIntOrNull() ?: 0 },
                        label = { Text("Maths") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Attempts & Accuracy
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = correctQ.toString(),
                        onValueChange = { correctQ = it.toIntOrNull() ?: 0 },
                        label = { Text("Correct Qs") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = incorrectQ.toString(),
                        onValueChange = { incorrectQ = it.toIntOrNull() ?: 0 },
                        label = { Text("Incorrect Qs") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Surface(
                    color = SurfaceElevated,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total: $totalObtained / 300", fontWeight = FontWeight.Bold, color = AccentEmerald)
                        Text(text = "Penalty: -$incorrectQ", color = AccentRose)
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Exam Retrospective / Notes") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = if (name.isBlank()) "Mock Test Result" else name
                    onAdd(finalName, date, totalMarks, totalObtained, physicsMarks, chemMarks, mathsMarks, attempted, correctQ, incorrectQ, unattempted, "JEE Main", notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Save Test")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddMistakeDialog(
    availableTests: List<MockTestEntity>,
    availableChapters: List<String>,
    onDismiss: () -> Unit,
    onAdd: (testId: Long, testName: String, subjectId: String, chapter: String, qNum: Int, type: String, desc: String, notes: String) -> Unit
) {
    var subject by remember { mutableStateOf("physics") }
    var chapter by remember { mutableStateOf(availableChapters.firstOrNull() ?: "Kinematics") }
    var questionNum by remember { mutableIntStateOf(12) }
    var mistakeType by remember { mutableStateOf("Conceptual") }
    var description by remember { mutableStateOf("") }
    var takeawayNotes by remember { mutableStateOf("") }

    val test = availableTests.firstOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Mistake in Notebook", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Subject selection
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("physics" to "Physics", "chemistry" to "Chemistry", "mathematics" to "Maths").forEach { (id, name) ->
                        FilterChip(
                            selected = subject == id,
                            onClick = { subject = id },
                            label = { Text(name, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = chapter,
                    onValueChange = { chapter = it },
                    label = { Text("Chapter Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = questionNum.toString(),
                    onValueChange = { questionNum = it.toIntOrNull() ?: 1 },
                    label = { Text("Question Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = "Mistake Category:", style = MaterialTheme.typography.labelSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf("Conceptual", "Calculation", "Interpretation", "Time Management", "Silly")) { type ->
                        FilterChip(
                            selected = mistakeType == type,
                            onClick = { mistakeType = type },
                            label = { Text(type, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("What mistake did you make?") },
                    placeholder = { Text("e.g. Applied wrong sign in Doppler formula") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = takeawayNotes,
                    onValueChange = { takeawayNotes = it },
                    label = { Text("Correct Rule / How to avoid next time?") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(test?.id ?: 1L, test?.testName ?: "Mock Test", subject, chapter, questionNum, mistakeType, description, takeawayNotes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Log Mistake")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
