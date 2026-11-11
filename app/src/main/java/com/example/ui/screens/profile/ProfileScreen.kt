package com.example.ui.screens.profile

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
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
import com.example.ui.components.GlowCard
import com.example.ui.screens.auth.avatarOptions
import com.example.ui.theme.*
import com.example.ui.viewmodel.JeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: JeeViewModel,
    onBack: () -> Unit,
    onRevisitTutorial: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val syllabusSummary by viewModel.syllabusSummary.collectAsStateWithLifecycle()
    val streak by viewModel.currentStreak.collectAsStateWithLifecycle()
    val mockTests by viewModel.mockTests.collectAsStateWithLifecycle()
    val backlogs by viewModel.activeBacklogs.collectAsStateWithLifecycle()

    val user = userProfile
    val userName = user?.name ?: "Aspirant"
    val userEmail = user?.email ?: "student@example.com"
    val examName = user?.examName ?: "JEE Main 2027 & Advanced"
    val avatarType = user?.avatarType ?: "rocket"
    var dailyTargetHours by remember(user) { mutableFloatStateOf(user?.dailyTargetHours ?: 6f) }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showDemoLoadedSnackbar by remember { mutableStateOf(false) }

    val (avatarIcon, avatarColor) = when (avatarType.lowercase()) {
        "atom" -> Pair(Icons.Filled.Science, ChemistryColor)
        "graduate" -> Pair(Icons.Filled.School, AccentGold)
        "trophy" -> Pair(Icons.Filled.EmojiEvents, AccentGold)
        "fire" -> Pair(Icons.Filled.LocalFireDepartment, AccentRose)
        "brain" -> Pair(Icons.Filled.Psychology, ElectricVioletLight)
        else -> Pair(Icons.Filled.RocketLaunch, ElectricBlueLight)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Aspirant Profile & Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showEditProfileDialog = true }) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit Profile", tint = ElectricBlueLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
            )
        },
        containerColor = BgDark,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
        ) {
            // Profile Header Card
            item {
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(avatarColor.copy(alpha = 0.2f))
                                .border(2.dp, avatarColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = avatarIcon,
                                contentDescription = "Avatar Photo",
                                tint = avatarColor,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
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
                                    Icon(Icons.Filled.Flag, contentDescription = null, tint = AccentGold, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = examName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AccentGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { showEditProfileDialog = true }) {
                            Icon(Icons.Filled.EditNote, contentDescription = "Edit Profile", tint = ElectricBlueLight)
                        }
                    }
                }
            }

            // Stats Overview
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatPillBox("Study Streak", "$streak Days", Icons.Filled.LocalFireDepartment, AccentGold, Modifier.weight(1f))
                    StatPillBox("Syllabus", "${syllabusSummary.overallPercentage}%", Icons.AutoMirrored.Filled.MenuBook, ElectricBlueLight, Modifier.weight(1f))
                    StatPillBox("Mock Tests", "${mockTests.size}", Icons.Filled.Insights, ChemistryColor, Modifier.weight(1f))
                }
            }

            // Daily Target Hours Setting
            item {
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Daily Target Study Hours", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = "${dailyTargetHours.toInt()} Hours", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ElectricBlueLight)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = dailyTargetHours,
                        onValueChange = {
                            dailyTargetHours = it
                            viewModel.updateProfile(userName, examName, avatarType, it)
                        },
                        valueRange = 4f..12f,
                        steps = 7,
                        colors = SliderDefaults.colors(thumbColor = ElectricBlueLight, activeTrackColor = ElectricBlue)
                    )
                }
            }

            // App Tour & Tutorial
            item {
                GlowCard(
                    onClick = onRevisitTutorial,
                    borderColor = ElectricBlue.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
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
                                    .background(ElectricBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = ElectricBlueLight, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "App Tutorial & Feature Tour", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(text = "Review how to use Syllabus, Backlogs & Timer", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextTertiary)
                    }
                }
            }

            // Data & Offline Management
            item {
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Data & Account Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Local SQLite database: 100% functional offline\n• Real-time persistent storage for your custom syllabus, tests, and backlogs\n• Clean slate for new accounts",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showExportDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Stats")
                        }

                        Button(
                            onClick = {
                                viewModel.loadDemoSampleData()
                                showDemoLoadedSnackbar = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated, contentColor = ElectricBlueLight),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Load Demo")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onLogout,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentGold),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Switch Account")
                        }

                        Button(
                            onClick = { showResetConfirm = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRose.copy(alpha = 0.25f), contentColor = AccentRose),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Filled.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Clear Data")
                        }
                    }

                    if (showDemoLoadedSnackbar) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "✓ Sample tests & sessions loaded for testing!", style = MaterialTheme.typography.labelSmall, color = AccentEmerald)
                    }
                }
            }

            // About App
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "JEE Companion v1.0", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Plan Smarter. Study Better. Crack JEE.", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                }
            }
        }
    }

    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(userName) }
        var editExam by remember { mutableStateOf(examName) }
        var editAvatar by remember { mutableStateOf(avatarType) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Student Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Student Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editExam,
                        onValueChange = { editExam = it },
                        label = { Text("Target Exam Name") },
                        placeholder = { Text("e.g. JEE Main 2027 & Advanced") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(text = "Select Avatar Icon:", style = MaterialTheme.typography.labelSmall)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(avatarOptions) { opt ->
                            val isSelected = editAvatar == opt.id
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) opt.color.copy(alpha = 0.3f) else SurfaceElevated)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) opt.color else SurfaceBorder,
                                        shape = CircleShape
                                    )
                                    .clickable { editAvatar = opt.id },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(opt.icon, contentDescription = opt.label, tint = opt.color, modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(editName, editExam, editAvatar, dailyTargetHours)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Study Report Summary", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("=== JEE COMPANION REPORT ===", fontWeight = FontWeight.Bold, color = ElectricBlueLight)
                    Text("• Student: $userName")
                    Text("• Target Exam: $examName")
                    Text("• Overall Syllabus Completion: ${syllabusSummary.overallPercentage}% (${syllabusSummary.completedCount}/${syllabusSummary.totalCount} chapters)")
                    Text("  - Physics: ${syllabusSummary.physicsPercentage}%")
                    Text("  - Chemistry: ${syllabusSummary.chemistryPercentage}%")
                    Text("  - Mathematics: ${syllabusSummary.mathsPercentage}%")
                    Text("• Active Backlogs: ${backlogs.size}")
                    Text("• Mock Tests Logged: ${mockTests.size}")
                    Text("• Consistency Streak: $streak Days")
                }
            },
            confirmButton = {
                Button(onClick = { showExportDialog = false }) { Text("Done") }
            }
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Clear All Records?", fontWeight = FontWeight.Bold, color = AccentRose) },
            text = {
                Text("This will wipe all study sessions, backlogs, mock tests, and mistakes, resetting your syllabus to 0% progress for a completely fresh start. Are you sure?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetToCleanNewUser(userName, userEmail, avatarType, examName)
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRose)
                ) {
                    Text("Reset All to New")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun StatPillBox(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
        }
    }
}
