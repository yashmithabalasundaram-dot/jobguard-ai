package com.jobguardai.jobguardai.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldSecondary,
    secondary = FreshTealAccent,
    tertiary = MintGreenHighlight,
    background = DarkCharcoalText,
    surface = Color(0xFF1B2420),
    onPrimary = SurfaceLight,
    onSecondary = SurfaceLight,
    onBackground = SurfaceLight,
    onSurface = SurfaceLight
)

private val LightColorScheme = lightColorScheme(
    primary = DeepTealPrimary,
    secondary = EmeraldSecondary,
    tertiary = FreshTealAccent,
    background = WarmWhiteBackground,
    surface = PureWhiteCard,
    onPrimary = SurfaceLight,
    onSecondary = SurfaceLight,
    onBackground = DarkCharcoalText,
    onSurface = DarkCharcoalText
)

@Composable
fun JobguardAiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
