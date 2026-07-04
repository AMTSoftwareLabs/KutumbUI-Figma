package com.kutumb.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutumb.app.R
import com.kutumb.app.domain.LoyaltyLevel
import com.kutumb.app.domain.getLoyaltyLevel
import com.kutumb.app.domain.getNextLevel
import com.kutumb.app.domain.levelProgress
import com.kutumb.app.ui.theme.*

// ── Top App Bar ────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KutumbTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // App icon — place drawable/kutumb_icon.png in res/drawable
                Icon(
                    painter = painterResource(R.drawable.kutumb_icon),
                    contentDescription = "Kutumb",
                    modifier = Modifier.size(28.dp).clip(RoundedCornerShape(7.dp)),
                    tint = Color.Unspecified
                )
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.White)
            }
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg),
        actions = actions
    )
}

// ── Dark hero wrapper ──────────────────────────────────────────────────────
@Composable
fun DarkHeroSection(
    seed: SeedTheme,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier.fillMaxWidth()) {
        // Radial glow in top-right
        Box(
            Modifier
                .size(220.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-60).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(seed.primary.copy(alpha = 0.18f), Color.Transparent)
                    ),
                    CircleShape
                )
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(DarkBg, DarkSurface)))
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 28.dp),
            content = content
        )
        // Curved bottom transition into screen background
        Box(
            Modifier
                .fillMaxWidth()
                .height(28.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.background)
        )
    }
}

// ── Stat box used inside DarkHeroSection ──────────────────────────────────
@Composable
fun HeroStatBox(icon: String, value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(icon, fontSize = 16.sp)
            Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp, lineHeight = 20.sp)
            Text(label, color = Color(0xFF64748B), fontSize = 10.sp)
        }
    }
}

// ── Card with left color border ────────────────────────────────────────────
@Composable
fun LeftBorderCard(
    borderColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ElevatedCard(modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Row {
            Box(Modifier.width(4.dp).fillMaxHeight().background(borderColor))
            Box(Modifier.weight(1f).padding(14.dp)) { content() }
        }
    }
}

// ── Gradient-fill button ───────────────────────────────────────────────────
@Composable
fun GradientButton(
    text: String,
    seed: SeedTheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) seed.gradientBrush else Brush.linearGradient(listOf(Color(0xFF64748B), Color(0xFF64748B))))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

// ── Member initial avatar ──────────────────────────────────────────────────
@Composable
fun MemberAvatar(
    shortName: String,
    colorHex: String,
    size: Dp = 36.dp,
    fontSize: TextUnit = 13.sp
) {
    Box(
        Modifier.size(size).background(Color(android.graphics.Color.parseColor(colorHex)), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(shortName, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = fontSize)
    }
}

// ── Compact chip ───────────────────────────────────────────────────────────
@Composable
fun SmallChip(text: String, containerColor: Color, textColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(containerColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}

// ── Level progress bar ─────────────────────────────────────────────────────
@Composable
fun LevelProgressBar(currentScore: Int, modifier: Modifier = Modifier) {
    val level   = getLoyaltyLevel(currentScore)
    val nextLv  = getNextLevel(currentScore)
    val progress= levelProgress(currentScore)

    Column(modifier) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${level.emoji} ${level.label}", color = Color(0xFF475569), fontSize = 10.sp)
            if (nextLv != null)
                Text("${nextLv.minPts - currentScore} pts और", color = level.color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = level.color,
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}

// ── Loyalty milestone strip ────────────────────────────────────────────────
@Composable
fun LevelMilestoneStrip(currentScore: Int, levels: List<LoyaltyLevel>, modifier: Modifier = Modifier) {
    val currentLevel = getLoyaltyLevel(currentScore)
    Row(modifier.fillMaxWidth()) {
        levels.forEachIndexed { i, lv ->
            val reached   = currentScore >= lv.minPts
            val isCurrent = currentLevel.label == lv.label
            Box(Modifier.weight(1f)) {
                if (i < levels.size - 1) {
                    Box(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .height(3.dp)
                            .align(Alignment.CenterEnd)
                            .offset(x = 16.dp, y = (-8).dp)
                            .background(if (reached) lv.color else MaterialTheme.colorScheme.outline)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .size(32.dp)
                            .background(
                                if (reached) lv.color else MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                            .then(if (isCurrent) Modifier.border(2.5.dp, lv.color, CircleShape) else Modifier),
                        contentAlignment = Alignment.Center
                    ) { Text(if (reached) lv.emoji else "○", fontSize = 14.sp) }
                    Spacer(Modifier.height(4.dp))
                    Text(lv.label, fontSize = 8.sp, fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (isCurrent) lv.color else Color(0xFF64748B), textAlign = TextAlign.Center)
                    Text("${lv.minPts}+", fontSize = 8.sp, color = Color(0xFF475569))
                }
            }
        }
    }
}

// ── Empty state ────────────────────────────────────────────────────────────
@Composable
fun EmptyState(emoji: String, title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().padding(vertical = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(emoji, fontSize = 56.sp)
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

// ── Section header row ─────────────────────────────────────────────────────
@Composable
fun SectionHeader(title: String, trailing: String = "", onTrailingClick: () -> Unit = {}) {
    Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
        if (trailing.isNotEmpty()) {
            Text(trailing, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                color = LocalSeedTheme.current.primary,
                modifier = Modifier.clickable(onClick = onTrailingClick))
        }
    }
}

// ── Snackbar helper ────────────────────────────────────────────────────────
enum class SnackSeverity { SUCCESS, ERROR, INFO }

@Composable
fun KutumbSnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(hostState) { data ->
        Snackbar(
            snackbarData = data,
            shape = RoundedCornerShape(14.dp),
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor   = MaterialTheme.colorScheme.inverseOnSurface
        )
    }
}
