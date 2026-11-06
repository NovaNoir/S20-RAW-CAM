package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Sophisticated Dark Design Palette (Tailwind neutral + amber accent)
val Neutral950 = Color(0xFF09090B) // Deep canvas
val Neutral900 = Color(0xFF141416) // Surface container / cards
val Neutral850 = Color(0xFF1C1C20) // Surface variant / pills
val Neutral800 = Color(0xFF27272A) // Subtle borders & dividers
val Neutral700 = Color(0xFF3F3F46) // Muted icons & boundaries
val Neutral600 = Color(0xFF52525B) // Inactive items
val Neutral500 = Color(0xFF71717A) // Metadata labels
val Neutral400 = Color(0xFFA1A1AA) // Secondary body text
val Neutral300 = Color(0xFFD4D4D8)
val Neutral200 = Color(0xFFE4E4E7) // High readability primary text
val Neutral100 = Color(0xFFF4F4F5)
val PureWhite = Color(0xFFFFFFFF)

// Accent & Glow
val Amber500 = Color(0xFFF59E0B) // Primary warm amber
val Amber400 = Color(0xFFFBBF24) // Bright amber highlight
val AmberContainer = Color(0xFF261908) // Deep amber container
val AmberGlow = Color(0x26F59E0B) // 15% amber halo
val BorderWhite10 = Color(0x1AFFFFFF) // Reticle & card border (white/10)
val ReticleWhite20 = Color(0x33FFFFFF) // Crosshair / tick lines (white/20)

// Aliases for compatibility
val ObsidianBg = Neutral950
val ObsidianSurface = Neutral900
val ObsidianSurfaceVariant = Neutral850
val ObsidianBorder = Neutral800

val CameraAmber = Amber500
val CameraAmberContainer = AmberContainer
val CameraAmberText = Amber400

val CameraCyan = Color(0xFF38BDF8)
val CameraCyanContainer = Color(0xFF0C2C3D)

val CameraRed = Color(0xFFF87171)
val CameraGreen = Color(0xFF4ADE80)

val TextPrimary = Neutral200
val TextSecondary = Neutral400
val TextMuted = Neutral500

// High-Contrast Light Design Palette (Crisp Daylight / Outdoor Sun Visibility)
val LightCanvas = Color(0xFFF8FAFC) // Slate 50 clean daylight canvas
val LightCardSurface = Color(0xFFFFFFFF) // Pure white card surface
val LightSurfaceVariant = Color(0xFFF1F5F9) // Slate 100 pill / HUD background
val LightBorder = Color(0xFFCBD5E1) // Slate 300 crisp border for daylight definition
val LightBorderSubtle = Color(0xFFE2E8F0) // Slate 200
val LightTextPrimary = Color(0xFF0F172A) // Slate 900 maximum contrast in direct sunlight
val LightTextSecondary = Color(0xFF334155) // Slate 700 readable secondary text
val LightTextMuted = Color(0xFF64748B) // Slate 500 metadata labels
val LightAmberPrimary = Color(0xFFD97706) // Amber 600 - bold, high-contrast warm amber
val LightAmberHighlight = Color(0xFFB45309) // Amber 700
val LightAmberContainer = Color(0xFFFEF3C7) // Amber 100 sunny container
val LightAmberGlow = Color(0x33D97706)
val LightCyan = Color(0xFF0284C7) // Sky 600 - high-visibility cyan
val LightCyanContainer = Color(0xFFE0F2FE) // Sky 100
val LightGreen = Color(0xFF16A34A) // Green 600
val LightRed = Color(0xFFDC2626) // Red 600

// Helper function to get color for Kelvin value
fun kelvinToColor(kelvin: Int): Color {
    return when {
        kelvin <= 3200 -> Color(0xFFFF8E3C)
        kelvin <= 4000 -> Color(0xFFFFAE68)
        kelvin <= 5000 -> Color(0xFFFFD1A4)
        kelvin <= 5500 -> Color(0xFFFFF3E8)
        kelvin <= 6000 -> Color(0xFFE9F0FF)
        kelvin <= 6500 -> Color(0xFFD4E4FF)
        else -> Color(0xFFB8D3FF)
    }
}

