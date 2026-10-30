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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary
import com.jobguardai.jobguardai.ui.theme.WarningRed
import com.jobguardai.jobguardai.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var resetToken by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var isTokenSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
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
                text = "Back to Login",
                fontSize = 16.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (!isTokenSent) "Reset Password" else "Enter New Password",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (!isTokenSent)
                "Enter your registered email address to receive a secure password reset token."
            else
                "Enter the reset token sent to your email along with your desired new password.",
            fontSize = 14.sp,
            color = TextSecondary,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        if (!isTokenSent) {
            InputField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                label = "Registered Email",
                placeholder = "alex@example.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage!!, color = WarningRed, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = if (isLoading) "Generating Token..." else "Send Reset Token",
                onClick = {
                    if (email.isBlank()) {
                        errorMessage = "Please enter your email"
                        return@PrimaryButton
                    }
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        val res = authViewModel.forgotPassword(email)
                        isLoading = false
                        res.onSuccess { token ->
                            resetToken = token
                            isTokenSent = true
                            Toast.makeText(context, "Reset token generated! Please set your new password.", Toast.LENGTH_LONG).show()
                        }.onFailure { e ->
                            errorMessage = e.message ?: "Failed to generate token"
                        }
                    }
                }
            )
        } else {
            InputField(
                value = resetToken,
                onValueChange = {
                    resetToken = it
                    errorMessage = null
                },
                label = "Reset Token",
                placeholder = "Enter reset token"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = null
                },
                label = "New Password",
                placeholder = "Minimum 8 characters",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage!!, color = WarningRed, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = if (isLoading) "Updating Password..." else "Update Password",
                onClick = {
                    if (resetToken.isBlank() || newPassword.isBlank()) {
                        errorMessage = "Please fill in all fields"
                        return@PrimaryButton
                    }
                    if (newPassword.length < 8) {
                        errorMessage = "Password must be at least 8 characters"
                        return@PrimaryButton
                    }
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        val res = authViewModel.resetPassword(resetToken, newPassword)
                        isLoading = false
                        res.onSuccess {
                            Toast.makeText(context, "Password updated successfully! Please login.", Toast.LENGTH_LONG).show()
                            navController.navigate(ScreenRoutes.Login.route) {
                                popUpTo(ScreenRoutes.ResetPassword.route) { inclusive = true }
                            }
                        }.onFailure { e ->
                            errorMessage = e.message ?: "Failed to reset password"
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { isTokenSent = false },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Resend or Change Email", color = DeepTealPrimary, fontSize = 14.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordScreenPreview() {
    JobguardAiTheme {
        ResetPasswordScreen(rememberNavController())
    }
}
