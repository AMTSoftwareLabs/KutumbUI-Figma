package com.kutumb.app.ui.screens.niyama

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutumb.app.data.model.Reward
import com.kutumb.app.data.model.Rule
import com.kutumb.app.domain.getLoyaltyLevel
import com.kutumb.app.ui.components.*
import com.kutumb.app.ui.theme.LocalSeedTheme
import com.kutumb.app.ui.viewmodel.MainViewModel
import com.kutumb.app.ui.viewmodel.uuid

private val RULE_CATS = listOf("स्वास्थ्य","अनुशासन","परिवार","स्वच्छता","शिक्षा","वित्त","अन्य")
private val PALETTE = listOf(
    Pair("#FF6B35","#FFF3EE"), Pair("#6366F1","#EEF2FF"), Pair("#10B981","#ECFDF5"),
    Pair("#F59E0B","#FFFBEB"), Pair("#EC4899","#FDF2F8"), Pair("#8B5CF6","#F5F3FF")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NiyamaScreen(viewModel: MainViewModel) {
    val state = by viewModel.niyamaState.collectAsState()
    val seed  = LocalSeedTheme.current
    var tab   by remember { mutableIntStateOf(0) }
    val snack = remember { SnackbarHostState() }

    // Dialog states
    var showAddRule   by remember { mutableStateOf(false) }
    var showAddReward by remember { mutableStateOf(false) }
    var rateDialog    by remember { mutableStateOf<Triple<Rule, Boolean, String>?>(null) } // rule, followed, selectedUserId
    var redeemDialog  by remember { mutableStateOf<Reward?>(null) }
    var delRule       by remember { mutableStateOf<Rule?>(null) }
    var rateUser      by remember { mutableStateOf(state.members.firstOrNull()?.id ?: "raj") }
    var redeemUser    by remember { mutableStateOf(state.members.firstOrNull()?.id ?: "raj") }

    val rewards   = state.rewards.filter { it.type == "reward" }
    val punishes  = state.rewards.filter { it.type == "punishment" }
    val myScore   = state.loyaltyScores[state.members.firstOrNull { it.role == "admin" }?.id ?: "raj"] ?: 100
    val myLevel   = getLoyaltyLevel(myScore)

    LaunchedEffect(state.snack) { state.snack?.let { snack.showSnackbar(it) } }

    Scaffold(
        topBar = {
            KutumbTopBar(
                title = listOf("नियम","पुरस्कार","दंड")[tab],
                actions = {
                    AssistChip(onClick = {}, label = { Text("${myLevel.emoji} ${myLevel.label}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = myLevel.color.copy(0.2f), labelColor = myLevel.color))
                    Spacer(Modifier.width(8.dp))
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { if (tab == 0) showAddRule = true else showAddReward = true },
                containerColor = seed.primary, contentColor = Color.White) { Icon(Icons.Default.Add, null) }
        },
        snackbarHost = { KutumbSnackbarHost(snack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()), contentPadding = PaddingValues(bottom = 100.dp)) {
            // Hero
            item {
                DarkHeroSection(seed) {
                    // Level progress
                    val nextLv = com.kutumb.app.domain.getNextLevel(myScore)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        Column {
                            Text(myLevel.emoji, fontSize = 22.sp)
                            Text("$myScore", color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp, lineHeight = 28.sp)
                            Text(myLevel.label, color = myLevel.color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        if (nextLv != null)
                            Text("${nextLv.minPts - myScore} pts और", color = Color(0xFF475569), fontSize = 11.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { com.kutumb.app.domain.levelProgress(myScore) },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = myLevel.color, trackColor = Color.White.copy(0.08f)
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HeroStatBox("📜","${state.rules.size}","नियम",    seed.primary, Modifier.weight(1f))
                        HeroStatBox("🎁","${rewards.size}","पुरस्कार", Color(0xFF10B981), Modifier.weight(1f))
                        HeroStatBox("⚡","${punishes.size}","दंड",       Color(0xFFEF4444), Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // Tabs
            item {
                TabRow(selectedTabIndex = tab, containerColor = MaterialTheme.colorScheme.surface,
                    indicator = { pos -> TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(pos[tab]), color = seed.primary, height = 2.5.dp) }
                ) {
                    listOf("📜 नियम (${state.rules.size})","🎁 पुरस्कार (${rewards.size})","⚡ दंड (${punishes.size})").forEachIndexed { i, label ->
                        Tab(selected = tab == i, onClick = { tab = i },
                            text = { Text(label, fontWeight = if (tab == i) FontWeight.ExtraBold else FontWeight.Medium, fontSize = 12.sp) },
                            selectedContentColor = seed.primary, unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // ── Tab 0: Rules ──────────────────────────────────────────────
            if (tab == 0) {
                if (state.rules.isEmpty()) item { EmptyState("📋","कोई नियम नहीं","नया नियम जोड़ने के लिए + दबाएं") }
                items(state.rules, key = { it.id }) { rule ->
                    Spacer(Modifier.height(8.dp))
                    LeftBorderCard(Color(android.graphics.Color.parseColor(rule.colorHex)), Modifier.padding(horizontal = 16.dp)) {
                        Column {
                            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(Modifier.size(40.dp).background(Color(android.graphics.Color.parseColor(rule.bgColorHex)).copy(if (MaterialTheme.colorScheme.background == com.kutumb.app.ui.theme.DarkBg) 0.3f else 1f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center) { Text("📜", fontSize = 17.sp) }
                                Column(Modifier.weight(1f)) {
                                    Text(rule.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        SmallChip(rule.category, Color(android.graphics.Color.parseColor(rule.colorHex)).copy(0.15f), Color(android.graphics.Color.parseColor(rule.colorHex)))
                                        SmallChip("±${rule.pointWeight} pts", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                IconButton(onClick = { delRule = rule }, Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, null, Modifier.size(15.dp), tint = Color(0xFFEF4444).copy(0.5f))
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // माना button
                                Button(onClick = { rateUser = state.members.firstOrNull()?.id ?: "raj"; rateDialog = Triple(rule, true, rateUser) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981).copy(0.12f), contentColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(vertical = 7.dp)
                                ) {
                                    Icon(Icons.Default.ThumbUp, null, Modifier.size(14.dp)); Spacer(Modifier.width(4.dp))
                                    Text("माना", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                // तोड़ा button
                                Button(onClick = { rateUser = state.members.firstOrNull()?.id ?: "raj"; rateDialog = Triple(rule, false, rateUser) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(0.12f), contentColor = Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(vertical = 7.dp)
                                ) {
                                    Icon(Icons.Default.ThumbDown, null, Modifier.size(14.dp)); Spacer(Modifier.width(4.dp))
                                    Text("तोड़ा", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // ── Tab 1: Rewards ────────────────────────────────────────────
            if (tab == 1) {
                item {
                    ElevatedCard(Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(20.dp)) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text("आपके उपलब्ध अंक", color = Color(0xFF475569), fontSize = 11.sp)
                                Text("$myScore", color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(myLevel.emoji, fontSize = 22.sp)
                                Text(myLevel.label, color = myLevel.color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
                if (rewards.isEmpty()) item { EmptyState("🎁","कोई पुरस्कार नहीं","नया पुरस्कार जोड़ें") }
                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.height((((rewards.size + 1) / 2) * 200).dp).padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(rewards) { rw ->
                            OutlinedCard(shape = RoundedCornerShape(20.dp)) {
                                Column(Modifier.padding(14.dp)) {
                                    Text(rw.emoji, fontSize = 28.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Text(rw.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(rw.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp, modifier = Modifier.padding(top = 2.dp, bottom = 8.dp))
                                    if (rw.redeemedById != null) {
                                        SmallChip("✓ रिडीम हो गया", Color(0xFF10B981).copy(0.15f), Color(0xFF10B981))
                                    } else {
                                        Button(onClick = { redeemUser = state.members.firstOrNull()?.id ?: "raj"; redeemDialog = rw },
                                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = if (myScore >= rw.cost) seed.primary else Color(0xFF64748B).copy(0.2f), contentColor = Color.White),
                                            contentPadding = PaddingValues(vertical = 6.dp)
                                        ) { Text("🎁 ${rw.cost} pts", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Tab 2: Punishments ────────────────────────────────────────
            if (tab == 2) {
                item {
                    Card(Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("⚠️", fontSize = 20.sp)
                            Text("नियम तोड़ने पर दंड स्वतः लागू होता है।", fontSize = 12.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                if (punishes.isEmpty()) item { EmptyState("⚡","कोई दंड नहीं","नया दंड जोड़ने के लिए + दबाएं") }
                items(punishes, key = { it.id }) { pw ->
                    Spacer(Modifier.height(8.dp))
                    LeftBorderCard(Color(0xFFEF4444), Modifier.padding(horizontal = 16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(pw.emoji, fontSize = 22.sp)
                            Column(Modifier.weight(1f)) {
                                Text(pw.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(pw.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(4.dp))
                                SmallChip("${pw.cost} pts", Color(0xFFEF4444).copy(0.15f), Color(0xFFEF4444))
                            }
                            Button(onClick = { redeemUser = state.members.firstOrNull()?.id ?: "raj"; redeemDialog = pw },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(0.12f), contentColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) { Text("लागू करें", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
        }
    }

    // ── माना/तोड़ा dialog ─────────────────────────────────────────────────
    rateDialog?.let { (rule, followed, _) ->
        var selectedUser by remember { mutableStateOf(state.members.firstOrNull()?.id ?: "raj") }
        AlertDialog(
            onDismissRequest = { rateDialog = null },
            title = {
                Column {
                    Text(if (followed) "किसने माना?" else "किसने तोड़ा?", fontWeight = FontWeight.ExtraBold)
                    Text(rule.title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            text = {
                Column {
                    state.members.forEach { m ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedUser == m.id, onClick = { selectedUser = m.id })
                            Spacer(Modifier.width(8.dp))
                            MemberAvatar(m.shortName, m.colorHex, 28.dp, 11.sp)
                            Spacer(Modifier.width(8.dp))
                            Text("${m.name} (${state.loyaltyScores[m.id] ?: 100} pts)", fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rateRule(rule.id, selectedUser, followed)
                        rateDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (followed) Color(0xFF10B981) else Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (followed) "✓ माना (+${rule.pointWeight} pts)"
                        else "✗ तोड़ा (−${rule.pointWeight / 2} pts)",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = { TextButton(onClick = { rateDialog = null }) { Text("रद्द") } },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // ── Redeem dialog ──────────────────────────────────────────────────────
    redeemDialog?.let { rw ->
        var selectedUser by remember { mutableStateOf(state.members.firstOrNull()?.id ?: "raj") }
        AlertDialog(
            onDismissRequest = { redeemDialog = null },
            title = { Column { Text(rw.emoji, fontSize = 24.sp); Text(rw.title, fontWeight = FontWeight.ExtraBold) } },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(rw.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    state.members.forEach { m ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedUser == m.id, onClick = { selectedUser = m.id })
                            Spacer(Modifier.width(8.dp))
                            MemberAvatar(m.shortName, m.colorHex, 26.dp, 10.sp)
                            Spacer(Modifier.width(8.dp))
                            Text("${m.name} (${state.loyaltyScores[m.id] ?: 100} pts)", fontSize = 13.sp)
                        }
                    }
                    if (rw.type == "reward") {
                        val uScore = state.loyaltyScores[selectedUser] ?: 100
                        if (uScore < rw.cost) {
                            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB))) {
                                Text("⚠️ अपर्याप्त अंक! $uScore / ${rw.cost} pts", fontSize = 12.sp, color = Color(0xFFF59E0B), modifier = Modifier.padding(10.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.redeemReward(rw, selectedUser); redeemDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = if (rw.type == "punishment") Color(0xFFEF4444) else seed.primary),
                    shape = RoundedCornerShape(12.dp)
                ) { Text(if (rw.type == "punishment") "⚡ दंड स्वीकार करें" else "🎁 रिडीम करें", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { redeemDialog = null }) { Text("रद्द") } },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // ── Delete rule confirm ────────────────────────────────────────────────
    delRule?.let { rule ->
        AlertDialog(
            onDismissRequest = { delRule = null },
            title = { Text("नियम हटाएं?", fontWeight = FontWeight.ExtraBold) },
            text  = { Text("यह नियम स्थायी रूप से हट जाएगा।") },
            confirmButton = { TextButton(onClick = { viewModel.deleteRule(rule); delRule = null }) { Text("हटाएं", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { delRule = null }) { Text("रद्द") } }
        )
    }

    // ── Add rule dialog ────────────────────────────────────────────────────
    if (showAddRule) AddRuleDialog(seed = seed, onDismiss = { showAddRule = false }, onSave = { viewModel.addRule(it); showAddRule = false })

    // ── Add reward dialog ──────────────────────────────────────────────────
    if (showAddReward) AddRewardDialog(seed = seed, isPunishment = tab == 2, onDismiss = { showAddReward = false }, onSave = { viewModel.addReward(it); showAddReward = false })
}

@Composable
private fun AddRuleDialog(seed: com.kutumb.app.ui.theme.SeedTheme, onDismiss: () -> Unit, onSave: (Rule) -> Unit) {
    var title     by remember { mutableStateOf("") }
    var category  by remember { mutableStateOf(RULE_CATS.first()) }
    var weight    by remember { mutableFloatStateOf(30f) }
    var colorIdx  by remember { mutableIntStateOf(0) }
    var titleErr  by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("नया नियम जोड़ें", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it; titleErr = false }, label = { Text("नियम का विवरण *") },
                    isError = titleErr, supportingText = if (titleErr) { { Text("विवरण आवश्यक है") } } else null,
                    modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 3)
                // Category
                var catExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = it }) {
                    OutlinedTextField(value = category, onValueChange = {}, label = { Text("श्रेणी") }, readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(catExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(), singleLine = true)
                    ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                        RULE_CATS.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { category = it; catExpanded = false }) }
                    }
                }
                // Weight slider
                Text("अंक भार: ${weight.toInt()}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Slider(value = weight, onValueChange = { weight = it }, valueRange = 5f..100f, steps = 18,
                    colors = SliderDefaults.colors(thumbColor = seed.primary, activeTrackColor = seed.primary))
                // Color picker
                Text("रंग चुनें", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PALETTE.forEachIndexed { i, (hex, _) ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        Box(Modifier.size(28.dp).background(color, RoundedCornerShape(14.dp))
                            .then(if (colorIdx == i) Modifier.border(2.dp, Color.White, RoundedCornerShape(14.dp)) else Modifier)
                            .clickable { colorIdx = i })
                    }
                }
            }
        },
        confirmButton = {
            GradientButton("नियम सहेजें ✓", seed, onClick = {
                if (title.isBlank()) { titleErr = true; return@GradientButton }
                onSave(Rule(id = uuid(), title = title.trim(), category = category,
                    pointWeight = weight.toInt(), colorHex = PALETTE[colorIdx].first, bgColorHex = PALETTE[colorIdx].second))
            })
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("रद्द करें") } },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun AddRewardDialog(seed: com.kutumb.app.ui.theme.SeedTheme, isPunishment: Boolean, onDismiss: () -> Unit, onSave: (Reward) -> Unit) {
    var title by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf(if (isPunishment) "⚡" else "🎁") }
    var desc  by remember { mutableStateOf("") }
    var cost  by remember { mutableStateOf("100") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isPunishment) "नया दंड जोड़ें" else "नया पुरस्कार जोड़ें", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = emoji, onValueChange = { emoji = it }, label = { Text("इमोजी") }, modifier = Modifier.width(80.dp), singleLine = true)
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("शीर्षक *") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("विवरण") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("लागत (pts)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            GradientButton("सहेजें ✓", seed, onClick = {
                val c = cost.toIntOrNull() ?: 100
                onSave(Reward(id = uuid(), title = title.trim(), emoji = emoji, description = desc.trim(),
                    cost = if (isPunishment) -c else c, type = if (isPunishment) "punishment" else "reward",
                    colorHex = if (isPunishment) "#EF4444" else seed.primary.toString(),
                    bgColorHex = if (isPunishment) "#FEF2F2" else "#ECFDF5"))
            })
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("रद्द करें") } },
        shape = RoundedCornerShape(24.dp)
    )
}
