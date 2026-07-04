package com.kutumb.app.ui.screens.rina

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
import com.kutumb.app.data.model.Loan
import com.kutumb.app.data.model.LoanPayment
import com.kutumb.app.ui.components.*
import com.kutumb.app.ui.theme.LocalSeedTheme
import com.kutumb.app.ui.viewmodel.MainViewModel
import com.kutumb.app.ui.viewmodel.todayIso
import com.kutumb.app.ui.viewmodel.uuid

private val LOAN_COLORS = listOf("#6366F1","#10B981","#F59E0B","#EC4899","#0EA5E9","#FF6B35")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RinaScreen(viewModel: MainViewModel) {
    val state  = by viewModel.rinaState.collectAsState()
    val seed   = LocalSeedTheme.current
    var tab    by remember { mutableIntStateOf(0) }
    var showAdd by remember { mutableStateOf(false) }
    var payLoan by remember { mutableStateOf<Loan?>(null) }
    val snack   = remember { SnackbarHostState() }

    val totalDebt      = state.loans.sumOf { it.remaining }
    val totalEmi       = state.loans.sumOf { it.emiAmount }
    val totalPrincipal = state.loans.sumOf { it.principal }
    val overallPct     = if (totalPrincipal > 0) ((totalPrincipal - totalDebt) / totalPrincipal).toFloat() else 0f

    Scaffold(
        topBar = { KutumbTopBar("ऋण") },
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
                    Text("कुल सक्रिय ऋण", color = Color(0xFF475569), fontSize = 12.sp)
                    Text("₹${String.format("%.1f", totalDebt / 100000)}L", color = Color.White, fontWeight = FontWeight.Black, fontSize = 36.sp, lineHeight = 36.sp, letterSpacing = (-1.5).sp)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("संपूर्ण प्रगति", color = Color(0xFF64748B), fontSize = 11.sp)
                        Text("${(overallPct * 100).toInt()}% चुकाया", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(progress = { overallPct }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF10B981), trackColor = Color.White.copy(0.08f))
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HeroStatBox("📅","₹${(totalEmi/1000).toInt()}K","मासिक EMI",  Color(0xFFF59E0B), Modifier.weight(1f))
                        HeroStatBox("🏦","${state.loans.size}","ऋण संख्या",         Color(0xFF6366F1), Modifier.weight(1f))
                        HeroStatBox("💳","${state.payments.size}","भुगतान",         Color(0xFF10B981), Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // Tabs
            item {
                TabRow(selectedTabIndex = tab, containerColor = MaterialTheme.colorScheme.surface,
                    indicator = { pos -> TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(pos[tab]), color = seed.primary, height = 2.5.dp) }) {
                    listOf("दृश्य","सक्रिय ऋण").forEachIndexed { i, label ->
                        Tab(selected = tab == i, onClick = { tab = i },
                            text = { Text(label, fontWeight = if (tab == i) FontWeight.ExtraBold else FontWeight.Medium) },
                            selectedContentColor = seed.primary, unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Empty
            if (state.loans.isEmpty()) item { EmptyState("🏦","कोई ऋण नहीं","नया ऋण जोड़ने के लिए + दबाएं") }

            // Loan cards
            items(state.loans, key = { it.id }) { loan ->
                val pct = if (loan.principal > 0) ((loan.principal - loan.remaining) / loan.principal).toFloat() else 0f
                val color = Color(android.graphics.Color.parseColor(loan.colorHex))
                Spacer(Modifier.height(8.dp))
                LeftBorderCard(color, Modifier.padding(horizontal = 16.dp)) {
                    Column {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column {
                                Text(loan.name, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                Spacer(Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    SmallChip(loan.bank, color.copy(0.15f), color)
                                    SmallChip("${loan.interestRate}% ब्याज", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            SmallChip("सक्रिय", Color(0xFF10B981).copy(0.15f), Color(0xFF10B981))
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("₹${String.format("%.1f",loan.remaining/100000)}L शेष", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${(pct*100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
                        }
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(progress = { pct }, modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(4.dp)),
                            color = color, trackColor = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("${loan.startDate} – ${loan.endDate}", fontSize = 10.sp, color = Color(0xFF64748B))
                                Spacer(Modifier.height(2.dp))
                                SmallChip("EMI ₹${(loan.emiAmount/1000).toInt()}K/माह", color.copy(0.12f), color)
                            }
                            if (tab == 1) {
                                Button(onClick = { payLoan = loan }, colors = ButtonDefaults.buttonColors(containerColor = seed.primary, contentColor = Color.White),
                                    shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp)) {
                                    Icon(Icons.Default.Payment, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp))
                                    Text("भुगतान", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Payment history (tab 1 only)
            if (tab == 1 && state.payments.isNotEmpty()) {
                item { Spacer(Modifier.height(16.dp)); Text("📜 भुगतान इतिहास", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 16.dp)) }
                item {
                    ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(20.dp)) {
                        state.payments.forEachIndexed { i, p ->
                            val loanName = state.loans.find { it.id == p.loanId }?.name ?: "ऋण"
                            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(Modifier.size(36.dp).background(Color(0xFF10B981).copy(0.12f), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) { Text("💳", fontSize = 16.sp) }
                                Column(Modifier.weight(1f)) {
                                    Text(loanName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("${p.date}${if (p.note.isNotEmpty()) " • ${p.note}" else ""}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("-₹${p.amount.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF10B981))
                            }
                            if (i < state.payments.size - 1) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }

    // Add loan dialog
    if (showAdd) AddLoanDialog(seed = seed, onDismiss = { showAdd = false }, onSave = { viewModel.addLoan(it); showAdd = false })

    // Payment dialog
    payLoan?.let { loan ->
        PaymentDialog(loan = loan, seed = seed, onDismiss = { payLoan = null },
            onSave = { payment -> viewModel.makePayment(payment, loan); payLoan = null })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddLoanDialog(seed: com.kutumb.app.ui.theme.SeedTheme, onDismiss: () -> Unit, onSave: (Loan) -> Unit) {
    var name      by remember { mutableStateOf("") }
    var bank      by remember { mutableStateOf("") }
    var principal by remember { mutableStateOf("") }
    var rate      by remember { mutableStateOf("") }
    var emi       by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("2025") }
    var colorIdx  by remember { mutableIntStateOf(0) }
    var nameErr   by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("नया ऋण जोड़ें", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it; nameErr = false }, label = { Text("ऋण का नाम *") },
                    isError = nameErr, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = bank, onValueChange = { bank = it }, label = { Text("बैंक / ऋणदाता") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = principal, onValueChange = { principal = it }, label = { Text("मूलधन ₹ *") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = rate, onValueChange = { rate = it }, label = { Text("ब्याज % *") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = emi, onValueChange = { emi = it }, label = { Text("EMI ₹ *") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("शुरुआत") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                Text("रंग चुनें", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LOAN_COLORS.forEachIndexed { i, hex ->
                        val c = Color(android.graphics.Color.parseColor(hex))
                        Box(Modifier.size(28.dp).background(c, RoundedCornerShape(14.dp))
                            .then(if (colorIdx == i) Modifier.border(2.dp, Color.White, RoundedCornerShape(14.dp)) else Modifier)
                            .clickable { colorIdx = i })
                    }
                }
            }
        },
        confirmButton = {
            GradientButton("ऋण सहेजें ✓", seed, onClick = {
                if (name.isBlank()) { nameErr = true; return@GradientButton }
                val p = principal.toDoubleOrNull() ?: return@GradientButton
                onSave(Loan(id = uuid(), name = name.trim(), bank = bank.ifBlank { "बैंक" },
                    principal = p, remaining = p, interestRate = rate.toDoubleOrNull() ?: 0.0,
                    emiAmount = emi.toDoubleOrNull() ?: 0.0, startDate = startDate, endDate = "2030",
                    colorHex = LOAN_COLORS[colorIdx]))
            })
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("रद्द करें") } },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun PaymentDialog(loan: Loan, seed: com.kutumb.app.ui.theme.SeedTheme, onDismiss: () -> Unit, onSave: (LoanPayment) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var date   by remember { mutableStateOf(todayIso()) }
    var note   by remember { mutableStateOf("") }
    var amtErr by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Column { Text("भुगतान करें", fontWeight = FontWeight.ExtraBold); Text(loan.name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = amount, onValueChange = { amount = it; amtErr = false },
                    label = { Text("राशि ₹ *") }, isError = amtErr,
                    supportingText = if (amtErr) { { Text("सही राशि दर्ज करें") } } else null,
                    modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("तारीख") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("नोट (वैकल्पिक)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            GradientButton("भुगतान करें ✓", seed, onClick = {
                val amt = amount.toDoubleOrNull()
                if (amt == null || amt <= 0) { amtErr = true; return@GradientButton }
                onSave(LoanPayment(id = uuid(), loanId = loan.id, amount = amt, date = date, note = note))
            })
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("रद्द") } },
        shape = RoundedCornerShape(24.dp)
    )
}

private fun androidx.compose.ui.Modifier.clickable(onClick: () -> Unit) =
    this.then(Modifier.clickable(onClick = onClick))

private fun androidx.compose.ui.Modifier.border(width: androidx.compose.ui.unit.Dp, color: Color, shape: androidx.compose.ui.graphics.Shape) =
    this.then(Modifier.border(width, color, shape))
