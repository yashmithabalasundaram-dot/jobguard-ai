package com.jobguardai.jobguardai.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobguardai.jobguardai.ui.theme.AccessibleErrorRed
import com.jobguardai.jobguardai.ui.theme.DarkCharcoalText
import com.jobguardai.jobguardai.ui.theme.DeepTealPrimary
import com.jobguardai.jobguardai.ui.theme.EmeraldSecondary
import com.jobguardai.jobguardai.ui.theme.MutedGreyGreenText
import com.jobguardai.jobguardai.ui.theme.SubtleGreyGreenBorder
import com.jobguardai.jobguardai.ui.theme.TypedBlackText

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier.widthIn(max = 600.dp).fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkCharcoalText
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = MutedGreyGreenText, fontSize = 14.sp) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            singleLine = singleLine,
            isError = isError,
            textStyle = TextStyle(
                color = TypedBlackText, // CRITICAL: Explicitly Black (#000000) for user-typed input
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TypedBlackText,        // Explicit black (#000000)
                unfocusedTextColor = TypedBlackText,      // Explicit black (#000000)
                disabledTextColor = Color(0xFF757575),
                focusedBorderColor = EmeraldSecondary,
                unfocusedBorderColor = SubtleGreyGreenBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = EmeraldSecondary,
                errorBorderColor = AccessibleErrorRed,
                focusedPlaceholderColor = MutedGreyGreenText,
                unfocusedPlaceholderColor = MutedGreyGreenText,
                focusedLabelColor = DeepTealPrimary,
                unfocusedLabelColor = MutedGreyGreenText
            )
        )
        if (isError && !errorMessage.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = AccessibleErrorRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
