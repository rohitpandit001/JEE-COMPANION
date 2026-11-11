package com.example.ui.screens.syllabus

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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChapterEntity
import com.example.ui.components.GlowCard
import com.example.ui.components.SubjectBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.JeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyllabusScreen(
    viewModel: JeeViewModel,
    modifier: Modifier = Modifier
) {
    val allChapters by viewModel.chapters.collectAsStateWithLifecycle()
    val syllabusSummary by viewModel.syllabusSummary.collectAsStateWithLifecycle()

    var selectedSubjectTab by remember { mutableStateOf("all") } // "all", "physics", "chemistry", "mathematics"
    var selectedClassFilter by remember { mutableIntStateOf(0) } // 0 = All, 11 = Class 11, 12 = Class 12
    var onlyHighYield by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedChapterForEdit by remember { mutableStateOf<ChapterEntity?>(null) }

    val filteredChapters = remember(allChapters, selectedSubjectTab, selectedClassFilter, onlyHighYield, searchQuery) {
        allChapters.filter { chap ->
            val matchSubject = selectedSubjectTab == "all" || chap.subjectId.equals(selectedSubjectTab, ignoreCase = true)
            val matchClass = selectedClassFilter == 0 || chap.classLevel == selectedClassFilter
            val matchHighYield = !onlyHighYield || chap.importance == "High"
            val matchSearch = searchQuery.isBlank() || chap.name.contains(searchQuery, ignoreCase = true) || chap.subCategory.contains(searchQuery, ignoreCase = true)
            matchSubject && matchClass && matchHighYield && matchSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title & Overall Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "JEE Syllabus Tracker",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "JEE Main & Advanced 2027 Syllabus",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Surface(
                color = ElectricBlue.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "${syllabusSummary.overallPercentage}% Completed",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ElectricBlueLight,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search chapters, e.g. Thermodynamics, Calculus...") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = TextSecondary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceCard,
                unfocusedContainerColor = SurfaceCard,
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = SurfaceBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("syllabus_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subject Tabs
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(
                listOf(
                    "all" to "All Subjects",
                    "physics" to "Physics (${syllabusSummary.physicsPercentage}%)",
                    "chemistry" to "Chemistry (${syllabusSummary.chemistryPercentage}%)",
                    "mathematics" to "Maths (${syllabusSummary.mathsPercentage}%)"
                )
            ) { (id, label) ->
                val isSelected = selectedSubjectTab == id
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedSubjectTab = id },
                    label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Secondary Filters: Class 11, Class 12, High Yield
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = selectedClassFilter == 11,
                onClick = { selectedClassFilter = if (selectedClassFilter == 11) 0 else 11 },
                label = { Text("Class 11th", style = MaterialTheme.typography.labelSmall) }
            )
            FilterChip(
                selected = selectedClassFilter == 12,
                onClick = { selectedClassFilter = if (selectedClassFilter == 12) 0 else 12 },
                label = { Text("Class 12th", style = MaterialTheme.typography.labelSmall) }
            )
            FilterChip(
                selected = onlyHighYield,
                onClick = { onlyHighYield = !onlyHighYield },
                label = { Text("🔥 High Yield", style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentGoldBg,
                    selectedLabelColor = AccentGold
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chapter List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            items(filteredChapters) { chapter ->
                ChapterCard(
                    chapter = chapter,
                    onClick = { selectedChapterForEdit = chapter }
                )
            }
        }
    }

    selectedChapterForEdit?.let { chapterToEdit ->
        EditChapterProgressSheet(
            chapter = chapterToEdit,
            onDismiss = { selectedChapterForEdit = null },
            onSave = { updated ->
                viewModel.updateChapter(updated)
                selectedChapterForEdit = null
            }
        )
    }
}

@Composable
private fun ChapterCard(
    chapter: ChapterEntity,
    onClick: () -> Unit
) {
    val (statusColor, statusBg) = when (chapter.status) {
        "Completed" -> Pair(AccentEmerald, AccentEmeraldBg)
        "Practicing" -> Pair(ElectricBlueLight, Color(0x263B82F6))
        "Learning" -> Pair(AccentGold, AccentGoldBg)
        "Revision Required" -> Pair(AccentRose, AccentRoseBg)
        else -> Pair(TextTertiary, SurfaceElevated)
    }

    GlowCard(
        onClick = onClick,
        borderColor = if (chapter.status == "Completed") AccentEmerald.copy(alpha = 0.35f) else SurfaceBorder,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SubjectBadge(subjectId = chapter.subjectId)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Class ${chapter.classLevel} • ${chapter.subCategory}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    if (chapter.importance == "High") {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = AccentGoldBg,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "High Weightage",
                                color = AccentGold,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = chapter.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Surface(
                color = statusBg,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
            ) {
                Text(
                    text = chapter.status,
                    color = statusColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress Metric Bars
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ChapterProgressMetric("Lectures", "${chapter.lectureCompletedPct}%", chapter.lectureCompletedPct / 100f, ElectricBlueLight, Modifier.weight(1f))
            ChapterProgressMetric("DPPs", "${chapter.dppSolvedPct}%", chapter.dppSolvedPct / 100f, ChemistryColor, Modifier.weight(1f))
            ChapterProgressMetric("PYQs", "${chapter.pyqSolvedPct}%", chapter.pyqSolvedPct / 100f, MathsColor, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Understanding rating (1-5 dots)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Clarity: ", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                for (i in 1..5) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (i <= chapter.conceptRating) AccentGold else SurfaceElevated,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Text(
                text = "Revised ${chapter.revisionCount}x",
                style = MaterialTheme.typography.labelSmall,
                color = if (chapter.revisionCount > 0) AccentEmerald else TextTertiary
            )
        }
    }
}

@Composable
private fun ChapterProgressMetric(
    label: String,
    value: String,
    fraction: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
            Text(text = value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { fraction },
            color = color,
            trackColor = SurfaceElevated,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditChapterProgressSheet(
    chapter: ChapterEntity,
    onDismiss: () -> Unit,
    onSave: (ChapterEntity) -> Unit
) {
    var status by remember { mutableStateOf(chapter.status) }
    var lecturePct by remember { mutableIntStateOf(chapter.lectureCompletedPct) }
    var dppPct by remember { mutableIntStateOf(chapter.dppSolvedPct) }
    var pyqPct by remember { mutableIntStateOf(chapter.pyqSolvedPct) }
    var conceptRating by remember { mutableIntStateOf(chapter.conceptRating) }
    var revisionCount by remember { mutableIntStateOf(chapter.revisionCount) }
    var notes by remember { mutableStateOf(chapter.notes) }

    val statusOptions = listOf("Not Started", "Learning", "Practicing", "Completed", "Revision Required")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    SubjectBadge(subjectId = chapter.subjectId)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = chapter.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            // Status selection
            Text(text = "Status", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(statusOptions) { opt ->
                    FilterChip(
                        selected = status == opt,
                        onClick = {
                            status = opt
                            if (opt == "Completed") {
                                lecturePct = 100
                                dppPct = maxOf(dppPct, 80)
                                pyqPct = maxOf(pyqPct, 75)
                            }
                        },
                        label = { Text(opt, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            // Sliders for Lecture, DPP, PYQ
            SliderMetricRow(label = "Lectures Completed ($lecturePct%)", value = lecturePct, onValueChange = { lecturePct = it })
            SliderMetricRow(label = "DPP Question Practice ($dppPct%)", value = dppPct, onValueChange = { dppPct = it })
            SliderMetricRow(label = "PYQ Practice ($pyqPct%)", value = pyqPct, onValueChange = { pyqPct = it })

            // Conceptual Understanding Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Conceptual Understanding", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Row {
                    for (i in 1..5) {
                        IconButton(
                            onClick = { conceptRating = i },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (i <= conceptRating) AccentGold else SurfaceElevated,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Revision counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Revision Count ($revisionCount times)", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (revisionCount > 0) revisionCount-- },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Remove, contentDescription = "Decrease")
                    }
                    Text(
                        text = "$revisionCount",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(
                        onClick = { revisionCount++ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Increase", tint = ElectricBlueLight)
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Formulas & Revision Notes") },
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    onSave(
                        chapter.copy(
                            status = status,
                            lectureCompletedPct = lecturePct,
                            dppSolvedPct = dppPct,
                            pyqSolvedPct = pyqPct,
                            conceptRating = conceptRating,
                            revisionCount = revisionCount,
                            lastRevisedDate = System.currentTimeMillis(),
                            notes = notes
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Chapter Progress", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SliderMetricRow(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = ElectricBlueLight,
                activeTrackColor = ElectricBlue
            )
        )
    }
}
