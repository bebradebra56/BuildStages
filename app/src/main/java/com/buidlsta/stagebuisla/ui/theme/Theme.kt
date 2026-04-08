package com.buidlsta.stagebuisla.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Orange40,
    onPrimary = Grey99,
    primaryContainer = Orange90,
    onPrimaryContainer = Orange10,
    secondary = Blue40,
    onSecondary = Grey99,
    secondaryContainer = Blue90,
    onSecondaryContainer = Blue10,
    tertiary = Green40,
    onTertiary = Grey99,
    tertiaryContainer = Green90,
    onTertiaryContainer = Green10,
    error = Red40,
    onError = Grey99,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Grey99,
    onBackground = Grey10,
    surface = Grey99,
    onSurface = Grey10,
    surfaceVariant = Grey95,
    onSurfaceVariant = Grey30,
    outline = Grey50,
    outlineVariant = Grey90
)

private val DarkColorScheme = darkColorScheme(
    primary = Orange80,
    onPrimary = Orange20,
    primaryContainer = Orange40,
    onPrimaryContainer = Orange90,
    secondary = Blue80,
    onSecondary = Blue20,
    secondaryContainer = Blue40,
    onSecondaryContainer = Blue90,
    tertiary = Green80,
    onTertiary = Green20,
    tertiaryContainer = Green40,
    onTertiaryContainer = Green90,
    error = Red80,
    onError = Red10,
    errorContainer = Red40,
    onErrorContainer = Red90,
    background = DarkBackground,
    onBackground = Grey90,
    surface = DarkSurface,
    onSurface = Grey90,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceMuted,
    outline = DarkOutline,
    outlineVariant = Grey30,
    inverseSurface = Grey90,
    inverseOnSurface = Grey10,
    scrim = Color(0xCC000000)
)

@Composable
fun BuildStagesTheme(
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
        typography = AppTypography,
        content = content
    )
}
