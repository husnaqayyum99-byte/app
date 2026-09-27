package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = JudicialNavy,
    onPrimary = WarmIvory,
    primaryContainer = JudicialNavyLight,
    onPrimaryContainer = WarmIvory,
    secondary = AntiqueGold,
    onSecondary = JudicialNavyDark,
    secondaryContainer = AntiqueGoldLight,
    onSecondaryContainer = JudicialNavyDark,
    tertiary = VerifiedGreen,
    onTertiary = WarmIvory,
    background = WarmPaper,
    onBackground = CharcoalText,
    surface = SurfaceCard,
    onSurface = CharcoalText,
    surfaceVariant = WarmIvory,
    onSurfaceVariant = CharcoalMuted,
    outline = BorderStone,
    outlineVariant = MutedStone,
    error = UrgentRed,
    onError = WarmIvory
)

private val DarkColorScheme = darkColorScheme(
    primary = AntiqueGoldLight,
    onPrimary = JudicialNavyDark,
    primaryContainer = JudicialNavyLight,
    onPrimaryContainer = WarmIvory,
    secondary = AntiqueGold,
    onSecondary = JudicialNavyDark,
    tertiary = VerifiedGreenLight,
    background = JudicialNavyDark,
    onBackground = WarmPaper,
    surface = JudicialNavy,
    onSurface = WarmPaper,
    surfaceVariant = JudicialNavyLight,
    onSurfaceVariant = BorderStone,
    outline = CharcoalMuted,
    error = UrgentRed
)

@Composable
fun ApnaWakeelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
