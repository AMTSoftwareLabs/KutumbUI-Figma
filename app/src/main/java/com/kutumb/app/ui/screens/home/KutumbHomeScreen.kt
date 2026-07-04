package com.kutumb.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutumb.app.data.model.Member
import com.kutumb.app.data.model.Task
import com.kutumb.app.domain.LOYALTY_LEVELS
import com.kutumb.app.domain.getLoyaltyLevel
import com.kutumb.app.ui.components.*
import com.kutumb.app.ui.theme.DarkBg
import com.kutumb.app.ui.theme.LocalSeedTheme
import com.kutumb.app.ui.viewmodel.MainViewModel

private val TIPS = listOf(
    "रात के भोजन पर फोन दूर रखें — परिवार का बंधन मजबूत होगा! 🙏",
    "सुबह साथ मिलकर पूजा करें — दिन अच्छा जाएगा। 🪔",
    "सप्ताह में एक बार परिवार के साथ खेल खेलें। 🎲",
    "घर की सफाई को आदत बनाएं — मन और घर दोनों खुश रहेंगे! 🏠",
)
private val RANK_MEDALS  = listOf("🥇","🥈","🥉","4️⃣","5️⃣")
private val MEDAL_COLORS = listOf(Color(0xFFF59E0B), Color(0xFF94A3B8), Color(0xFFCD7C3B), Color(0xFF64748B), Color(0xFF64748B))

@Composable
fun KutumbHomeScreen(viewModel: MainViewModel) {
    val state  by viewModel.homeState.collectAsState()
    val seed   = LocalSeedTheme.current
    var tipIdx by remember { mutableIntStateOf(0) }

    val myScore = state.loyaltyScores[state.currentUser.id] ?: 100
    val myLevel = getLoyaltyLevel(myScore)

    Scaffold(
        topBar = {
            KutumbTopBar(
                title = "Kutumb",
                actions = {
                    AssistChip(
                        onClick = {},
                        label = { Text("${myLevel.emoji} $myScore pts", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = seed.primary.copy(alpha = 0.2f),
                            labelColor     = seed.primary
                        ),
                        border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = seed.primary.copy(0.3f))
                    )
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFF475569))
                    }
                    MemberAvatar(state.currentUser.shortName, state.currentUser.colorHex, 30.dp, 12.sp)
                    Spacer(Modifier.width(8.dp))
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // ── Hero ────────────────────────────────────────────────────────
            item {
                DarkHeroSection(seed) {
                    Text("नमस्ते 🙏", color = Color(0xFF475569), fontSize = 12.sp)
                    Text(state.currentUser.name, color = Color.White, fontWeight = FontWeight.Black,
                        fontSize = 26.sp, letterSpacing = (-0.5).sp)
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HeroStatBox("🔥", "${state.streak}d", "स्ट्रीक", seed.primary, Modifier.weight(1f))
                        HeroStatBox("💰", "₹${(state.totalMonthlyExpense / 1000).toInt()}K", "व्यय", Color(0xFF6366F1), Modifier.weight(1f))
                        HeroStatBox("⭐", "$myScore", "स्कोर", Color(0xFF10B981), Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                    LevelProgressBar(myScore)
                }
            }

            // ── Padding below hero ──────────────────────────────────────────
            item { Spacer(Modifier.height(8.dp)) }

            // ── AI Tip ──────────────────────────────────────────────────────
            item {
                ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(20.dp)) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            Modifier.size(36.dp)
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFFBBF24))),
                                    RoundedCornerShape(11.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Default.Lightbulb, null, Modifier.size(20.dp), tint = Color.White) }
                        Column(Modifier.weight(1f)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("✨ AI सुझाव", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                                IconButton(onClick = { tipIdx = (tipIdx + 1) % TIPS.size }, Modifier.size(20.dp)) {
                                    Icon(Icons.Default.Refresh, null, Modifier.size(14.dp), tint = Color(0xFF94A3B8))
                                }
                            }
                            Text(TIPS[tipIdx], fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Leaderboard ─────────────────────────────────────────────────
            item {
                SectionHeader("🏆 लीडरबोर्ड", "${state.members?.size ?: state.leaderboard.size} सदस्य",
                    modifier = Modifier.padding(horizontal = 16.dp))
            }
            item {
                ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(20.dp)) {
                    state.leaderboard.forEachIndexed { i, (member, score) ->
                        val lvl = getLoyaltyLevel(score)
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(RANK_MEDALS[i], fontSize = 16.sp, modifier = Modifier.width(22.dp))
                            MemberAvatar(member.shortName, member.colorHex, 34.dp, 13.sp)
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(member.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(lvl.emoji, fontSize = 10.sp)
                                }
                                Spacer(Modifier.height(3.dp))
                                LinearProgressIndicator(
                                    progress = { (score / 600f).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = MEDAL_COLORS[i],
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("$score", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = MEDAL_COLORS[i])
                                Text("pts", fontSize = 9.sp, color = Color(0xFF64748B))
                            }
                        }
                        if (i < state.leaderboard.size - 1) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Today's tasks carousel ──────────────────────────────────────
            item {
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("📋 आज के कार्य", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("${state.todayTasks.count { it.isCompleted }}/${state.todayTasks.size} पूर्ण — tap to complete",
                            fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }
                    LinearProgressIndicator(
                        progress = { if (state.todayTasks.isEmpty()) 0f else state.todayTasks.count { it.isCompleted }.toFloat() / state.todayTasks.size },
                        modifier = Modifier.width(52.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF10B981),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
                Spacer(Modifier.height(10.dp))
            }
            item {
                if (state.todayTasks.isEmpty()) {
                    Text("आज के लिए कोई कार्य नहीं 🎉", fontSize = 13.sp, color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(horizontal = 16.dp))
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.todayTasks) { task ->
                            TodayTaskCard(task = task, seed = seed, onToggle = { viewModel.toggleTask(task) })
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Loyalty milestone strip ──────────────────────────────────────
            item {
                SectionHeader("🏅 स्तर का मार्ग", modifier = Modifier.padding(horizontal = 16.dp))
                ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(20.dp)) {
                    LevelMilestoneStrip(
                        currentScore = myScore,
                        levels = LOYALTY_LEVELS,
                        modifier = Modifier.padding(14.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Recent activity ──────────────────────────────────────────────
            item { SectionHeader("⚡ हाल की गतिविधियाँ", modifier = Modifier.padding(horizontal = 16.dp)) }
            item {
                val activities = listOf(
                    Triple("राज ने किराने का सामान खरीदा", "+50 pts", Color(0xFF10B981)),
                    Triple("माँ ने पूजा की", "+50 pts", Color(0xFF10B981)),
                    Triple("प्रिया ने झाड़ू लगाया", "+25 pts", Color(0xFF10B981)),
                    Triple("आर्यन ने होमवर्क किया", "+20 pts", Color(0xFF10B981)),
                )
                ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(20.dp)) {
                    activities.forEachIndexed { i, (text, pts, ptColor) ->
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MemberAvatar(text.first().toString(), "#FF6B35", 34.dp, 13.sp)
                            Column(Modifier.weight(1f)) {
                                Text(text, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp)
                                Text("2h पहले", fontSize = 10.sp, color = Color(0xFF475569))
                            }
                            SmallChip(pts, ptColor.copy(0.15f), ptColor)
                        }
                        if (i < activities.size - 1) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayTaskCard(task: Task, seed: com.kutumb.app.ui.theme.SeedTheme, onToggle: () -> Unit) {
    val bg = if (task.isCompleted)
        androidx.compose.ui.graphics.Brush.linearGradient(listOf(seed.primary.copy(0.22f), seed.primary.copy(0.10f)))
    else androidx.compose.ui.graphics.Brush.linearGradient(listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface))

    Box(
        Modifier
            .width(128.dp)
            .background(bg, RoundedCornerShape(18.dp))
            .border(1.5.dp, if (task.isCompleted) seed.primary.copy(0.5f) else MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .clickable(onClick = onToggle)
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(30.dp).background(
                        if (task.isCompleted) seed.primary else seed.primary.copy(0.15f),
                        RoundedCornerShape(9.dp)
                    ), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = null, tint = if (task.isCompleted) Color.White else seed.primary,
                        modifier = Modifier.size(17.dp)
                    )
                }
                SmallChip("+${task.points}", Color(0xFF10B981).copy(0.15f), Color(0xFF10B981))
            }
            Text(
                task.title,
                fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp,
                maxLines = 2, overflow = TextOverflow.Ellipsis,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(0.4f) else MaterialTheme.colorScheme.onSurface
            )
            Text(task.assignedToId, fontSize = 10.sp, color = seed.primary)
        }
    }
}

@Composable
private fun SectionHeader(title: String, trailing: String = "", modifier: Modifier = Modifier, onTrailingClick: () -> Unit = {}) {
    Row(modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
        if (trailing.isNotEmpty()) Text(trailing, fontSize = 12.sp, color = Color(0xFF94A3B8))
    }
}
