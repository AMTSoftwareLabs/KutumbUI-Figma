package com.kutumb.app.ui.screens.vyaya

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.kutumb.app.data.model.Expense
import com.kutumb.app.ui.components.*
import com.kutumb.app.ui.theme.LocalSeedTheme
import com.kutumb.app.ui.viewmodel.MainViewModel
import com.kutumb.app.ui.viewmodel.todayIso
import com.kutumb.app.ui.viewmodel.uuid

private data class ExpenseCat(val name: String, val icon: String, val colorHex: String)
private val EXPENSE_CATS = listOf(
    ExpenseCat("किराना","🛒","#FF6B35"), ExpenseCat("भोजन","🍽️","#10B981"),
    ExpenseCat("उपयोगिता","⚡","#F59E0B"), ExpenseCat("परिवहन","🚗","#6366F1"),
    ExpenseCat("स्वास्थ्य","💊","#EC4899"), ExpenseCat("शिक्षा","📚","#8B5CF6"),
    ExpenseCat("मनोरंजन","🎬","#0EA5E9"), ExpenseCat("आय","💰","#10B981"),
    ExpenseCat("अन्य","📦","#94A3B8"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VyayaScreen(viewModel: MainViewModel) {
    val state = by viewModel.vyayaState.collectAsState()
    val seed  = LocalSeedTheme.current
    var tab   by remember { mutableIntStateOf(0) }
    var showAdd by remember { mutableStateOf(false) }
    var delExp  by remember { mutableStateOf<Expense?>(null) }
    val snack   = remember { SnackbarHostState() }

    val totalExp = state.expenses.filter { !it.isIncome }.sumOf { it.amount }
    val totalInc = state.expenses.filter {  it.isIncome }.sumOf { it.amount }
    val limit    = state.monthlyLimit
    val usagePct = (totalExp / limit).toFloat().coerceIn(0f, 1f)
    val isNearLimit = usagePct >= 0.8f

    val filtered = when (tab) {
        1 -> state.expenses.filter { !it.isIncome }
        2 -> state.expenses.filter {  it.isIncome }
        else -> state.expenses
    }

    val rajPaid   = state.expenses.filter { !it.isIncome && it.paidById == "raj" }.sumOf { it.amount }
    val priyaPaid = state.expenses.filter { !it.isIncome && it.paidById == "priya" }.sumOf { it.amount }
    val diffAmt   = (rajPaid - priyaPaid) / 2

    Scaffold(
        topBar = { KutumbTopBar("व्यय") },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }, containerColor = seed.primary, contentColor = Color.White) { Icon(Icons.Default.Add, null) }
        },
        snackbarHost = { KutumbSnackbarHost(snack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()), contentPadding = PaddingValues(bottom = 100.dp)) {
            // Hero
            item {
                DarkHeroSection(seed) {
                    Text("इस महीने कुल व्यय", color = Color(0xFF475569), fontSize = 12.sp)
                    Text("₹${(totalExp / 1000).toInt()}K", color = Color.White, fontWeight = FontWeight.Black, fontSize = 36.sp, lineHeight = 36.sp, letterSpacing = (-1.5).sp)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HeroStatBox("📈","₹${(totalInc/1000).toInt()}K","साझा आय", Color(0xFF10B981), Modifier.weight(1f))
                        HeroStatBox("💰","₹${((totalInc-totalExp)/1000).toInt()}K","बचत", Color(0xFF6366F1), Modifier.weight(1f))
                        HeroStatBox("📊","${(usagePct*100).toInt()}%","बजट", Color(0xFFF59E0B), Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // Tabs
            item {
                TabRow(selectedTabIndex = tab, containerColor = MaterialTheme.colorScheme.surface,
                    indicator = { pos -> TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(pos[tab]), color = seed.primary, height = 2.5.dp) }) {
                    listOf("सभी","व्यय","आय").forEachIndexed { i, label ->
                        Tab(selected = tab == i, onClick = { tab = i }, text = { Text(label, fontWeight = if (tab == i) FontWeight.ExtraBold else FontWeight.Medium) },
                            selectedContentColor = seed.primary, unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Budget card
            item {
                ElevatedCard(Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("मासिक बजट", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("₹${(totalExp/1000).toInt()}K / ₹${(limit/1000).toInt()}K",
                                fontWeight = FontWeight.Bold, fontSize = 14.sp,
                                color = if (isNearLimit) Color(0xFFEF4444) else Color(0xFF10B981))
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { usagePct },
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                            color = if (isNearLimit) Color(0xFFEF4444) else Color(0xFF10B981),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))
                        // Settle up row
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MemberAvatar("R","#FF6B35",30.dp,12.sp)
                            Column(Modifier.weight(1f)) {
                                Text("हिसाब-किताब", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("राज ने ₹${diffAmt.toInt()} अधिक चुकाए", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = seed.primary), shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                                Text("Settle Up", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Header
            item { Text("${if (tab == 2) "आय" else "लेन-देन"} (${filtered.size})", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, modifier = Modifier.padding(start = 16.dp, bottom = 10.dp)) }

            // Empty
            if (filtered.isEmpty()) item { EmptyState("💸","कोई रिकॉर्ड नहीं","नया रिकॉर्ड जोड़ने के लिए + दबाएं") }

            // Expense rows
            item {
                ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(20.dp)) {
                    filtered.forEachIndexed { i, exp ->
                        val memberName = state.members.find { it.id == exp.paidById }?.name ?: exp.paidById
                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.size(40.dp).background(
                                Color(android.graphics.Color.parseColor(exp.categoryColorHex)).copy(if (exp.isIncome) 0.12f else 0.15f),
                                RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                                Text(exp.categoryIcon, fontSize = 17.sp)
                            }
                            Column(Modifier.weight(1f)) {
                                Text(exp.description, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("${exp.category} • ${exp.date} • $memberName", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${if (exp.isIncome) "+" else "-"}₹${exp.amount.toInt()}",
                                    fontWeight = FontWeight.ExtraBold, fontSize = 14.sp,
                                    color = if (exp.isIncome) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface)
                                IconButton(onClick = { delExp = exp }, Modifier.size(22.dp)) {
                                    Icon(Icons.Default.Delete, null, Modifier.size(13.dp), tint = Color(0xFFEF4444).copy(0.5f))
                                }
                            }
                        }
                        if (i < filtered.size - 1) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }

    if (showAdd) AddExpenseDialog(members = state.members, seed = seed, onDismiss = { showAdd = false }, onSave = { viewModel.addExpense(it); showAdd = false })

    delExp?.let { exp ->
        AlertDialog(onDismissRequest = { delExp = null },
            title = { Text("रिकॉर्ड हटाएं?", fontWeight = FontWeight.ExtraBold) },
            text  = { Text("यह सभी बैलेंस को पुनः गणना करेगा।") },
            confirmButton = { TextButton(onClick = { viewModel.deleteExpense(exp); delExp = null }) { Text("हटाएं", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { delExp = null }) { Text("रद्द") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseDialog(
    members: List<com.kutumb.app.data.model.Member>,
    seed: com.kutumb.app.ui.theme.SeedTheme,
    onDismiss: () -> Unit,
    onSave: (Expense) -> Unit
) {
    var amount    by remember { mutableStateOf("") }
    var desc      by remember { mutableStateOf("") }
    var catIdx    by remember { mutableIntStateOf(0) }
    var memberIdx by remember { mutableIntStateOf(0) }
    var isIncome  by remember { mutableStateOf(false) }
    var date      by remember { mutableStateOf(todayIso()) }
    var amtErr    by remember { mutableStateOf(false) }
    var descErr   by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("नया ${if (isIncome) "आय" else "व्यय"} जोड़ें", fontWeight = FontWeight.ExtraBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("आय", fontSize = 12.sp, color = if (isIncome) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant)
                    Switch(checked = isIncome, onCheckedChange = { isIncome = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF10B981), checkedTrackColor = Color(0xFF10B981).copy(0.5f)))
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = amount, onValueChange = { amount = it; amtErr = false },
                    label = { Text("राशि ₹ *") }, isError = amtErr,
                    supportingText = if (amtErr) { { Text("सही राशि दर्ज करें") } } else null,
                    modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = desc, onValueChange = { desc = it; descErr = false },
                    label = { Text("विवरण *") }, isError = descErr,
                    supportingText = if (descErr) { { Text("विवरण आवश्यक है") } } else null,
                    modifier = Modifier.fillMaxWidth(), singleLine = true)
                // Category dropdown
                var catExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = it }) {
                    OutlinedTextField(value = "${EXPENSE_CATS[catIdx].icon} ${EXPENSE_CATS[catIdx].name}", onValueChange = {}, label = { Text("श्रेणी") },
                        readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(catExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(), singleLine = true)
                    ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                        EXPENSE_CATS.forEachIndexed { i, cat ->
                            DropdownMenuItem(text = { Text("${cat.icon} ${cat.name}") }, onClick = { catIdx = i; catExpanded = false })
                        }
                    }
                }
                // Member dropdown
                var memExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = memExpanded, onExpandedChange = { memExpanded = it }) {
                    OutlinedTextField(value = members.getOrNull(memberIdx)?.name ?: "", onValueChange = {}, label = { Text("किसने चुकाया") },
                        readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(memExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(), singleLine = true)
                    ExposedDropdownMenu(expanded = memExpanded, onDismissRequest = { memExpanded = false }) {
                        members.forEachIndexed { i, m -> DropdownMenuItem(text = { Text(m.name) }, onClick = { memberIdx = i; memExpanded = false }) }
                    }
                }
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("तारीख") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            GradientButton("${if (isIncome) "आय" else "व्यय"} सहेजें ✓", seed, onClick = {
                val amt = amount.toDoubleOrNull()
                if (amt == null || amt <= 0) { amtErr = true; return@GradientButton }
                if (desc.isBlank()) { descErr = true; return@GradientButton }
                val cat = EXPENSE_CATS[catIdx]
                onSave(Expense(id = uuid(), amount = amt, description = desc.trim(),
                    category = cat.name, categoryColorHex = cat.colorHex, categoryIcon = cat.icon,
                    isIncome = isIncome, paidById = members.getOrNull(memberIdx)?.id ?: "raj",
                    splitType = "equal", date = date))
            })
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("रद्द करें") } },
        shape = RoundedCornerShape(24.dp)
    )
}
