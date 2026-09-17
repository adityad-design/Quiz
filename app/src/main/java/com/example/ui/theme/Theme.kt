package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.data.model.QuizMode

private val KidsLightScheme = lightColorScheme(
    primary = KidsPrimaryLight,
    onPrimary = KidsOnPrimaryLight,
    secondary = KidsSecondaryLight,
    tertiary = KidsTertiaryLight,
    background = KidsBackgroundLight,
    surface = KidsSurfaceLight,
    onSurface = KidsOnSurfaceLight
)

private val KidsDarkScheme = darkColorScheme(
    primary = KidsPrimaryDark,
    onPrimary = KidsOnPrimaryDark,
    secondary = KidsSecondaryDark,
    tertiary = KidsTertiaryDark,
    background = KidsBackgroundDark,
    surface = KidsSurfaceDark,
    onSurface = KidsOnSurfaceDark
)

private val AdultsLightScheme = lightColorScheme(
    primary = AdultsPrimaryLight,
    onPrimary = AdultsOnPrimaryLight,
    secondary = AdultsSecondaryLight,
    tertiary = AdultsTertiaryLight,
    background = AdultsBackgroundLight,
    surface = AdultsSurfaceLight,
    onSurface = AdultsOnSurfaceLight
)

private val AdultsDarkScheme = darkColorScheme(
    primary = AdultsPrimaryDark,
    onPrimary = AdultsOnPrimaryDark,
    secondary = AdultsSecondaryDark,
    tertiary = AdultsTertiaryDark,
    background = AdultsBackgroundDark,
    surface = AdultsSurfaceDark,
    onSurface = AdultsOnSurfaceDark
)

@Composable
fun QuizRushTheme(
    mode: QuizMode = QuizMode.KIDS,
    themeSetting: String = "SYSTEM",
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeSetting) {
        "LIGHT" -> false
        "DARK" -> true
        else -> isSystemDark
    }

    val colorScheme: ColorScheme = when (mode) {
        QuizMode.KIDS -> if (isDark) KidsDarkScheme else KidsLightScheme
        QuizMode.ADULTS -> if (isDark) AdultsDarkScheme else AdultsLightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
