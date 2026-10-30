package com.jobguardai.jobguardai.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jobguardai.jobguardai.ui.components.InputField
import com.jobguardai.jobguardai.ui.components.PrimaryButton
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.DeepTealPrimary
import com.jobguardai.jobguardai.ui.theme.EmeraldSecondary
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary
import com.jobguardai.jobguardai.ui.theme.WarningRed
import com.jobguardai.jobguardai.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsPrivacyScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var scamAlerts by remember { mutableStateOf(true) }
    var resumeConsent by remember { mutableStateOf(true) }

    var showPasswordForm by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isChangingPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Settings & Privacy",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy and Preferences Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = DeepTealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Security & Privacy Preferences", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "AI Scam Alerts", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                        Text(text = "Warn about suspicious jobs & unverified recruiters", fontSize = 12.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = scamAlerts,
                        onCheckedChange = { scamAlerts = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmeraldSecondary)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Verified Employer Visibility", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                        Text(text = "Allow verified recruiters to view your profile and resume", fontSize = 12.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = resumeConsent,
                        onCheckedChange = { resumeConsent = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmeraldSecondary)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password & Authentication Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = DeepTealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Account Security", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!showPasswordForm) {
                    OutlinedButton(
                        onClick = { showPasswordForm = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Change Password", color = DeepTealPrimary)
                    }
                } else {
                    InputField(
                        value = currentPassword,
                        onValueChange = {
                            currentPassword = it
                            passwordError = null
                        },
                        label = "Current Password",
                        placeholder = "••••••••",
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    InputField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            passwordError = null
                        },
                        label = "New Password",
                        placeholder = "Minimum 8 characters",
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    if (passwordError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = passwordError!!, color = WarningRed, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    PrimaryButton(
                        text = if (isChangingPassword) "Saving..." else "Save New Password",
                        onClick = {
                            if (currentPassword.isBlank() || newPassword.isBlank()) {
                                passwordError = "All fields are required"
                                return@PrimaryButton
                            }
                            if (newPassword.length < 8) {
                                passwordError = "New password must be at least 8 characters"
                                return@PrimaryButton
                            }
                            isChangingPassword = true
                            passwordError = null
                            coroutineScope.launch {
                                val res = authViewModel.changePassword(currentPassword, newPassword)
                                isChangingPassword = false
                                res.onSuccess {
                                    Toast.makeText(context, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                                    showPasswordForm = false
                                    currentPassword = ""
                                    newPassword = ""
                                }.onFailure { e ->
                                    passwordError = e.message ?: "Failed to change password"
                                }
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Out Button
        OutlinedButton(
            onClick = {
                authViewModel.logout()
                Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                navController.navigate(ScreenRoutes.Login.route) {
                    popUpTo(ScreenRoutes.HomeDashboard.route) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningRed)
        ) {
            Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = WarningRed)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Sign Out of JobGuard AI", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPrivacyScreenPreview() {
    JobguardAiTheme {
        SettingsPrivacyScreen(rememberNavController())
    }
}
