package com.example.wanderlust.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkScheme = darkColorScheme(
    primary = WanderlustDark.Primary,
    onPrimary = WanderlustDark.OnPrimaryContainer,
    primaryContainer = WanderlustDark.PrimaryContainer,
    onPrimaryContainer = WanderlustDark.OnPrimaryContainer,
    tertiary = WanderlustDark.Tertiary,
    background = WanderlustDark.Background,
    onBackground = WanderlustDark.OnBackground,
    surface = WanderlustDark.Surface,
    onSurface = WanderlustDark.OnSurface,
    surfaceVariant = WanderlustDark.SurfaceContainerHigh,
    onSurfaceVariant = WanderlustDark.OnSurfaceVariant,
    outline = WanderlustDark.Outline,
    outlineVariant = WanderlustDark.OutlineVariant,
    error = WanderlustDark.Error,
)

private val LightScheme = lightColorScheme(
    primary = WanderlustLight.Primary,
    onPrimary = Color.White,
    primaryContainer = WanderlustLight.PrimaryContainer,
    onPrimaryContainer = WanderlustLight.OnPrimaryContainer,
    tertiary = WanderlustLight.Tertiary,
    background = WanderlustLight.Background,
    onBackground = WanderlustLight.OnBackground,
    surface = WanderlustLight.Surface,
    onSurface = WanderlustLight.OnSurface,
    surfaceVariant = WanderlustLight.SurfaceContainerHigh,
    onSurfaceVariant = WanderlustLight.OnSurfaceVariant,
    outline = WanderlustLight.Outline,
    outlineVariant = WanderlustLight.OutlineVariant,
    error = WanderlustLight.Error,
)

@Composable
fun WanderlustTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkScheme else LightScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = scheme,
        typography = WanderlustTypography,
        content = content,
    )
}
