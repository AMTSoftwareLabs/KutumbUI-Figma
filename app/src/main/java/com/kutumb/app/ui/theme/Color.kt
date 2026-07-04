package com.kutumb.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Fixed backgrounds ──────────────────────────────────────────────────────
val DarkBg      = Color(0xFF0A0F1E)
val DarkSurface = Color(0xFF111827)
val DarkCard    = Color(0xFF111827)
val DarkBorder  = Color(0xFF1E293B)
val LightBg     = Color(0xFFF1F5F9)
val LightSurface= Color(0xFFFFFFFF)

// ── Semantic ───────────────────────────────────────────────────────────────
val ColorSuccess = Color(0xFF10B981)
val ColorError   = Color(0xFFEF4444)
val ColorWarning = Color(0xFFF59E0B)
val ColorInfo    = Color(0xFF64748B)
val ColorInfoDark= Color(0xFF94A3B8)

// ── Loyalty level colors ───────────────────────────────────────────────────
val LevelBronze  = Color(0xFFCD7C3B)
val LevelSilver  = Color(0xFF94A3B8)
val LevelGold    = Color(0xFFF59E0B)
val LevelDiamond = Color(0xFF38BDF8)
val LevelEmperor = Color(0xFFA78BFA)

// ── Seed themes ────────────────────────────────────────────────────────────
enum class SeedTheme(
    val hindiLabel: String,
    val emoji: String,
    val primary: Color,
    val secondary: Color,
    val id: String
) {
    AGNI   ("अग्नि",  "🔥", Color(0xFFFF6B35), Color(0xFFF7B32B), "agni"),
    INDIGO ("नील",    "💜", Color(0xFF6366F1), Color(0xFF8B5CF6), "indigo"),
    VANA   ("वन",     "🌿", Color(0xFF10B981), Color(0xFF34D399), "vana"),
    NEEL   ("नीला",   "💙", Color(0xFF0EA5E9), Color(0xFF38BDF8), "neel"),
    GULABI ("गुलाबी", "🌸", Color(0xFFEC4899), Color(0xFFF472B6), "gulabi"),
    RATRI  ("रात्रि", "🌙", Color(0xFF8B5CF6), Color(0xFFA78BFA), "ratri");

    fun glow(alpha: Float = 0.35f) = primary.copy(alpha = alpha)
}
