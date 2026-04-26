package com.autoexpert.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Petronas / Euro AEC Brand Palette ─────────────────────────────────────
val PetronasGreen     = Color(0xFF00A86B)
val PetronasGreenDark = Color(0xFF007A4D)
val PetronasGreenLight= Color(0xFFE6F7F0)
val AccentBlue        = Color(0xFF2563EB)
val AccentAmber       = Color(0xFFD97706)
val AccentRed         = Color(0xFFDC2626)
val AccentPurple      = Color(0xFF7C3AED)
val SurfaceDark       = Color(0xFF0F172A)
val SurfaceNavy       = Color(0xFF1E3A5F)
val CardWhite         = Color(0xFFFFFFFF)
val BackgroundGray    = Color(0xFFF3F4F6)
val TextPrimary       = Color(0xFF111827)
val TextSecondary     = Color(0xFF6B7280)
val TextDim           = Color(0xFF9CA3AF)
val BorderColor       = Color(0xFFE5E7EB)

private val LightColorScheme = lightColorScheme(
    primary          = PetronasGreen,
    onPrimary        = Color.White,
    primaryContainer = PetronasGreenLight,
    onPrimaryContainer = PetronasGreenDark,
    secondary        = AccentBlue,
    onSecondary      = Color.White,
    tertiary         = AccentAmber,
    background       = BackgroundGray,
    onBackground     = TextPrimary,
    surface          = CardWhite,
    onSurface        = TextPrimary,
    surfaceVariant   = BackgroundGray,
    onSurfaceVariant = TextSecondary,
    error            = AccentRed,
    onError          = Color.White,
    outline          = BorderColor,
)

@Composable
fun AutoExpertTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = AutoExpertTypography,
        content     = content
    )
}
