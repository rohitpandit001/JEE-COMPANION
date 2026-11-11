package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlowCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.JeeViewModel

data class AvatarOption(val id: String, val label: String, val icon: ImageVector, val color: Color)

val avatarOptions = listOf(
    AvatarOption("rocket", "Rocket", Icons.Filled.RocketLaunch, ElectricBlueLight),
    AvatarOption("atom", "Atom", Icons.Filled.Science, ChemistryColor),
    AvatarOption("graduate", "Graduate", Icons.Filled.School, AccentGold),
    AvatarOption("trophy", "Champion", Icons.Filled.EmojiEvents, AccentGold),
    AvatarOption("fire", "Focus Fire", Icons.Filled.LocalFireDepartment, AccentRose),
    AvatarOption("brain", "Genius", Icons.Filled.Psychology, ElectricVioletLight)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: JeeViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUp by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("Aspirant") }
    var email by remember { mutableStateOf("student@example.com") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var examName by remember { mutableStateOf("JEE Main 2027 & Advanced") }
    var selectedAvatar by remember { mutableStateOf("rocket") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Emblem
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(ElectricBlue.copy(alpha = 0.2f))
                .border(2.dp, ElectricBlueLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AutoStories,
                contentDescription = null,
                tint = ElectricBlueLight,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "JEE Companion",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp),
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            text = "Plan Smarter. Study Better. Crack JEE.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Auth Mode Switcher Tab
        TabRow(
            selectedTabIndex = if (isSignUp) 0 else 1,
            containerColor = SurfaceCard,
            contentColor = ElectricBlue,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = isSignUp,
                onClick = { isSignUp = true },
                text = { Text("Create Account / Sign Up", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = !isSignUp,
                onClick = { isSignUp = false },
                text = { Text("Log In", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GlowCard(modifier = Modifier.fillMaxWidth()) {
            if (isSignUp) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Full Name") },
                    placeholder = { Text("e.g. Aspirant") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = {
                        if (name.isNotEmpty()) {
                            IconButton(onClick = { name = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear Name", tint = TextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Avatar / Photo Picker
                Text(
                    text = "Choose Profile Avatar / Photo:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(avatarOptions) { opt ->
                        val isSelected = selectedAvatar == opt.id
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) opt.color.copy(alpha = 0.3f) else SurfaceElevated)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) opt.color else SurfaceBorder,
                                    shape = CircleShape
                                )
                                .clickable { selectedAvatar = opt.id }
                                .testTag("avatar_${opt.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = opt.icon, contentDescription = opt.label, tint = opt.color, modifier = Modifier.size(22.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Target Exam Name
                OutlinedTextField(
                    value = examName,
                    onValueChange = { examName = it },
                    label = { Text("Target Exam Name") },
                    placeholder = { Text("e.g. JEE Main 2027 & Advanced") },
                    leadingIcon = { Icon(Icons.Filled.Flag, contentDescription = null, tint = AccentGold) },
                    trailingIcon = {
                        if (examName.isNotEmpty()) {
                            IconButton(onClick = { examName = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear Exam Name", tint = TextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_exam_input")
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Email (Removable non-official dummy email with one-tap clear button)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                placeholder = { Text("student@example.com") },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = TextSecondary) },
                trailingIcon = {
                    if (email.isNotEmpty()) {
                        IconButton(onClick = { email = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear Email", tint = TextSecondary)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Password (Initially empty with visibility toggle)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = TextSecondary) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                            tint = TextSecondary
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input")
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    if (isSignUp) {
                        viewModel.resetToCleanNewUser(
                            name = name.ifBlank { "Aspirant" },
                            email = email.ifBlank { "student@example.com" },
                            avatarType = selectedAvatar,
                            examName = examName.ifBlank { "JEE Main 2027 & Advanced" }
                        )
                    } else {
                        viewModel.loginOrSignup(
                            name = name.ifBlank { "Aspirant" },
                            email = email.ifBlank { "student@example.com" },
                            avatarType = selectedAvatar,
                            examName = examName.ifBlank { "JEE Main 2027 & Advanced" }
                        )
                    }
                    onAuthSuccess()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("auth_submit_button")
            ) {
                Text(
                    text = if (isSignUp) "Create Account & Start Fresh" else "Log In & Restore My Records",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Guest / Skip for now
        TextButton(
            onClick = {
                viewModel.loginOrSignup(
                    name = "Aspirant",
                    email = "guest@jeeprep.in",
                    avatarType = "rocket",
                    examName = "JEE Main 2027 & Advanced"
                )
                onAuthSuccess()
            }
        ) {
            Text(
                text = "Continue as Guest / Offline Student",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
    }
}
