package com.jobguardai.jobguardai.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jobguardai.jobguardai.ui.components.PrimaryButton
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.BluePrimary
import com.jobguardai.jobguardai.ui.theme.BorderColor
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary
import com.jobguardai.jobguardai.viewmodel.ResumeViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun UploadResumeScreen(
    navController: NavController,
    resumeViewModel: ResumeViewModel = viewModel()
) {
    val context = LocalContext.current
    var consentChecked by remember { mutableStateOf(true) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var extractedFileText by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            var displayName = "Resume_${System.currentTimeMillis()}.pdf"
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        displayName = cursor.getString(nameIndex) ?: displayName
                    }
                }
                selectedFileName = displayName

                // Read content
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val reader = BufferedReader(InputStreamReader(stream))
                    val sb = StringBuilder()
                    var line: String? = reader.readLine()
                    var count = 0
                    while (line != null && count < 200) {
                        sb.append(line).append(" ")
                        line = reader.readLine()
                        count++
                    }
                    extractedFileText = sb.toString().trim()
                }
            } catch (e: Exception) {
                selectedFileName = displayName
                extractedFileText = "Resume Document: $displayName"
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        val maxContainerWidth = if (maxWidth > 600.dp) 650.dp else maxWidth

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .widthIn(max = maxContainerWidth)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Upload Resume",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Step 4 of 4: Select your actual PDF or DOCX resume for AI parsing and job matching.",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Dashed Drag & Drop Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, BorderColor, RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .clickable {
                        filePickerLauncher.launch("*/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BluePrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = selectedFileName ?: "Tap to choose your PDF or DOCX file",
                        fontWeight = FontWeight.Bold,
                        color = if (selectedFileName != null) BluePrimary else TextPrimary,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (selectedFileName != null) "File selected • Ready for upload & parsing" else "Supports PDF, DOCX, TXT (Max 5MB)",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = consentChecked,
                    onCheckedChange = { consentChecked = it },
                    colors = CheckboxDefaults.colors(checkedColor = BluePrimary)
                )
                Text(
                    text = "I consent to saving my resume in JobGuard AI and matching it against verified vacancies.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Start AI Resume Analysis",
                useGradient = true,
                onClick = {
                    val fileName = selectedFileName
                    if (fileName.isNullOrBlank()) {
                        Toast.makeText(context, "Please select a resume file first", Toast.LENGTH_SHORT).show()
                        filePickerLauncher.launch("*/*")
                        return@PrimaryButton
                    }

                    val textToSend = if (extractedFileText.isNotBlank()) extractedFileText else "Uploaded Resume: $fileName"

                    resumeViewModel.analyzeSelectedResume(
                        fileName = fileName,
                        uriString = selectedFileUri?.toString() ?: "",
                        rawContent = textToSend
                    )
                    navController.navigate(ScreenRoutes.AIAnalysisProgress.route)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UploadResumeScreenPreview() {
    JobguardAiTheme {
        UploadResumeScreen(rememberNavController())
    }
}
