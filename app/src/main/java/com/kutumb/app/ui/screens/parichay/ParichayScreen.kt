package com.kutumb.app.ui.screens.parichay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutumb.app.domain.LOYALTY_LEVELS
import com.kutumb.app.domain.getLoyaltyLevel
import com.kutumb.app.domain.getNextLevel
import com.kutumb.app.domain.levelProgress
import com.kutumb.app.ui.components.*
import com.kutumb.app.ui.theme.*
import com.kutumb.app.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParichayScreen(viewModel: MainViewModel) {
    val state    by viewModel.parichayState.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    val seed     = LocalSeedTheme.current

    val currentUser = state.members.firstOrNull { it.role == "admin" } ?: state.members.firstOrNull()
    val myScore  = state.loyaltyScores[currentUser?.id ?: "raj"] ?: 100
    val myLevel  = getLoyaltyLevel(myScore)
    val nextLv   = getNextLevel(myScore)
    val progress = levelProgress(myScore)

    var editOpen by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(currentUser?.name ?: "राज") }
    var editBio  by remember { mutableStateOf("परिवार का मुखिया 🏠") }
    val snack    = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            KutumbTopBar(
                title = "परिचय",
                actions = {
                    IconButton(onClick = { editOpen = true }) {
                        Icon(Icons.Default.Edit, null, tint = Color(0xFF475569))
                    }
                }
            )
        },
        snackbarHost = { KutumbSnackbarHost(snack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile header
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        Modifier.size(68.dp).clip(CircleShape)
                            .background(seed.gradientBrush),
                        contentAlignment = Alignment.Center
                    ) { Text(currentUser?.shortName ?: "R", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp) }
                    Column(Modifier.weight(1f)) {
                        Text(editName, fontWeight = FontWeight.Black, fontSize = 19.sp, letterSpacing = (-0.3).sp)
                        Text(editBio, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            AssistChip(onClick = {}, label = { Text("${myLevel.emoji} ${myLevel.label}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = myLevel.color.copy(0.18f), labelColor = myLevel.color))
                            AssistChip(onClick = {}, label = { Text("${seed.emoji} ${seed.hindiLabel}", fontSize = 11.sp) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = seed.primary.copy(0.18f), labelColor = seed.primary))
                        }
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline)
                }
            }

            // Loyalty card
            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkBg), shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.LocalFlorist, null, tint = seed.primary, modifier = Modifier.size(20.dp))
                            Text("कुटुम्ब निष्ठा वाटिका", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                        Spacer(Modifier.height(14.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                            Column {
                                Text("कुल अंक", color = Color(0xFF475569), fontSize = 11.sp)
                                Text("$myScore", color = Color.White, fontWeight = FontWeight.Black, fontSize = 32.sp, lineHeight = 32.sp, letterSpacing = (-1).sp)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(myLevel.emoji, fontSize = 16.sp)
                                    Text(myLevel.label, color = myLevel.color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                if (nextLv != null) {
                                    AssistChip(onClick = {},
                                        leadingIcon = { Icon(Icons.Default.EmojiEvents, null, Modifier.size(12.dp), tint = Color(0xFFF59E0B)) },
                                        label = { Text("${nextLv.emoji} ${nextLv.label} के लिए", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFF59E0B).copy(0.15f), labelColor = Color(0xFFF59E0B)))
                                    Spacer(Modifier.height(4.dp))
                                    Text("${nextLv.minPts - myScore} अंक और", color = Color(0xFF475569), fontSize = 10.sp)
                                } else {
                                    AssistChip(onClick = {}, label = { Text("👑 सम्राट!", fontSize = 11.sp, fontWeight = FontWeight.Black) },
                                        colors = AssistChipDefaults.assistChipColors(containerColor = LevelEmperor.copy(0.2f), labelColor = LevelEmperor))
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Box(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(Color.White.copy(0.07f))) {
                            Box(Modifier.fillMaxHeight().fillMaxWidth(progress)
                                .background(Brush.horizontalGradient(listOf(myLevel.color, myLevel.color.copy(0.6f)))))
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${myLevel.minPts}", color = Color(0xFF334155), fontSize = 10.sp)
                            Text(if (nextLv != null) "${nextLv.minPts}" else "MAX", color = Color(0xFF334155), fontSize = 10.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth(),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Color.White.copy(0.12f), Color.White.copy(0.12f)))),
                            shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Default.History, null, modifier = Modifier.size(16.dp), tint = Color(0xFF94A3B8))
                            Spacer(Modifier.width(6.dp))
                            Text("पुरस्कार इतिहास", color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Theme picker
            item {
                ElevatedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("🎨 थीम चुनें", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        Spacer(Modifier.height(14.dp))
                        // Dark / light toggle
                        Row(Modifier.fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(if (settings.isDarkMode) Icons.Default.DarkMode else Icons.Default.WbSunny, null, tint = if (settings.isDarkMode) Color(0xFF6366F1) else Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                                Text(if (settings.isDarkMode) "डार्क मोड" else "लाइट मोड", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                            Switch(checked = settings.isDarkMode, onCheckedChange = { viewModel.setDarkMode(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF6366F1), checkedTrackColor = Color(0xFF6366F1).copy(0.5f)))
                        }
                        Spacer(Modifier.height(14.dp))
                        Text("रंग थीम", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp))
                        // Seed grid (2 rows of 3)
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            SeedTheme.values().toList().chunked(3).forEach { row ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    row.forEach { s ->
                                        val isActive = s == settings.seedTheme
                                        Box(
                                            Modifier.weight(1f).aspectRatio(1.2f)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(if (isActive) s.gradientBrush else Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant)))
                                                .then(if (!isActive) Modifier.border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)) else Modifier)
                                                .clickable { viewModel.setSeedTheme(s) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(s.emoji, fontSize = 20.sp)
                                                Text(s.hindiLabel, fontSize = 11.sp, fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.SemiBold, color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(s.name, fontSize = 9.sp, color = if (isActive) Color.White.copy(0.7f) else Color(0xFF94A3B8))
                                            }
                                        }
                                    }
                                    // Fill empty slots
                                    repeat(3 - row.size) { Box(Modifier.weight(1f)) }
                                }
                            }
                        }
                    }
                }
            }

            // Family members
            item {
                ElevatedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("👨‍👩‍👧‍👦 परिवार के सदस्य", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 12.dp))
                        state.members.forEachIndexed { i, m ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                MemberAvatar(m.shortName, m.colorHex, 36.dp, 14.sp)
                                Column(Modifier.weight(1f)) {
                                    Text(m.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text(if (m.role == "admin") "प्रशासक" else "सदस्य", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                val s = state.loyaltyScores[m.id] ?: 100
                                val lv = getLoyaltyLevel(s)
                                AssistChip(onClick = {}, label = { Text("$s pts", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = AssistChipDefaults.assistChipColors(containerColor = lv.color.copy(0.15f), labelColor = lv.color))
                            }
                            if (i < state.members.size - 1) HorizontalDivider()
                        }
                    }
                }
            }

            // Sync / pair card
            item {
                ElevatedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 14.dp)) {
                            Box(Modifier.size(40.dp).background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.QrCode, null, tint = Color(0xFF3B82F6))
                            }
                            Column {
                                Text("Wi-Fi P2P सिंक", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("परिवार को ऑफलाइन जोड़ें", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)).padding(16.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Box(Modifier.size(80.dp).background(DarkBg, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Text("📱", fontSize = 36.sp) }
                                Spacer(Modifier.height(10.dp))
                                Text("युग्मन कोड", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(6.dp))
                                Box(Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp)).border(1.5.dp, MaterialTheme.colorScheme.outline.copy(0.5f), RoundedCornerShape(10.dp)).padding(horizontal = 16.dp, vertical = 8.dp)) {
                                    Text("KUT-8472-FARM", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, letterSpacing = 1.5.sp)
                                }
                                Spacer(Modifier.height(12.dp))
                                Button(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                                    Icon(Icons.Default.Share, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp))
                                    Text("QR कोड साझा करें", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit profile dialog
    if (editOpen) {
        AlertDialog(onDismissRequest = { editOpen = false },
            title = { Text("प्रोफ़ाइल संपादित करें", fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("नाम") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = editBio, onValueChange = { editBio = it }, label = { Text("बायो") }, modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 3)
                }
            },
            confirmButton = { GradientButton("सहेजें ✓", seed, onClick = { editOpen = false }) },
            dismissButton = { TextButton(onClick = { editOpen = false }) { Text("रद्द") } },
            shape = RoundedCornerShape(24.dp)
        )
    }
}
