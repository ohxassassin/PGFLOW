package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = PrimaryIndigoContainer,
    onPrimaryContainer = Color.White,
    secondary = ActionBlue,
    onSecondary = PrimaryIndigoContainer,
    secondaryContainer = ActionBlueContainer,
    onSecondaryContainer = Color.White,
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    background = OffWhiteCanvas,
    onBackground = TextColorDark,
    surface = PureWhiteCard,
    onSurface = TextColorDark,
    surfaceVariant = ActionBlueContainer,
    onSurfaceVariant = TextColorMuted,
    outline = OutlineColor,
    outlineVariant = OutlineVariantColor,
    error = DangerRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = ActionBlue,
    onPrimary = PrimaryIndigoContainer,
    primaryContainer = PrimaryIndigoContainer,
    onPrimaryContainer = Color.White,
    secondary = ActionBlue,
    onSecondary = Color.Black,
    secondaryContainer = ActionBlueContainer,
    onSecondaryContainer = Color.White,
    background = OffWhiteCanvas,
    onBackground = TextColorDark,
    surface = PureWhiteCard,
    onSurface = TextColorDark,
    surfaceVariant = ActionBlueContainer,
    onSurfaceVariant = TextColorMuted,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Both light and dark theme of the app are styled with our premium unified geometric violet theme
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
