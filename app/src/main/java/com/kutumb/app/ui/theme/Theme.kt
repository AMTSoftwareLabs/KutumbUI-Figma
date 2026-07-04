package com.kutumb.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ── Local seed access inside Composables ───────────────────────────────────
val LocalSeedTheme = staticCompositionLocalOf { SeedTheme.AGNI }

@Composable
fun KutumbTheme(
    darkTheme: Boolean = false,
    seedTheme: SeedTheme = SeedTheme.AGNI,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary          = seedTheme.primary,
            onPrimary        = Color.White,
            primaryContainer = seedTheme.primary.copy(alpha = 0.2f),
            secondary        = seedTheme.secondary,
            background       = DarkBg,
            surface          = DarkCard,
            surfaceVariant   = DarkSurface,
            outline          = DarkBorder,
            onBackground     = Color(0xFFF1F5F9),
            onSurface        = Color(0xFFF1F5F9),
            onSurfaceVariant = ColorInfoDark,
        )
    } else {
        lightColorScheme(
            primary          = seedTheme.primary,
            onPrimary        = Color.White,
            primaryContainer = seedTheme.primary.copy(alpha = 0.12f),
            secondary        = seedTheme.secondary,
            background       = LightBg,
            surface          = LightSurface,
            surfaceVariant   = Color(0xFFF8FAFC),
            outline          = Color(0xFFE2E8F0),
            onBackground     = Color(0xFF0F172A),
            onSurface        = Color(0xFF0F172A),
            onSurfaceVariant = ColorInfo,
        )
    }

    val shapes = Shapes(
        small      = RoundedCornerShape(8.dp),
        medium     = RoundedCornerShape(12.dp),
        large      = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(20.dp),
    )

    CompositionLocalProvider(LocalSeedTheme provides seedTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = KutumbTypography,
            shapes      = shapes,
            content     = content
        )
    }
}

// ── Gradient helpers ───────────────────────────────────────────────────────
val SeedTheme.gradientBrush: Brush
    get() = Brush.linearGradient(colors = listOf(primary, secondary))

val SeedTheme.heroGradient: Brush
    get() = Brush.linearGradient(colors = listOf(DarkBg, DarkSurface))
