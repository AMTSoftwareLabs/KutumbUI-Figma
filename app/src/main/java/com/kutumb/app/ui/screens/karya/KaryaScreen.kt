package com.kutumb.app.ui.screens.karya

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutumb.app.data.model.Task
import com.kutumb.app.domain.todayIso
import com.kutumb.app.ui.components.*
import com.kutumb.app.ui.theme.LocalSeedTheme
import com.kutumb.app.ui.viewmodel.MainViewModel
import com.kutumb.app.ui.viewmodel.uuid

private val CATS   = listOf("सफाई","धुलाई","खरीदारी","खाना","धर्म","शिक्षा","वित्त","अन्य")
private val FREQS  = listOf("Daily","Weekly","Monthly","Once")
private val FREQ_LABELS = mapOf("Daily" to "दैनिक","Weekly" to "साप्ताहिक","Monthly" to "मासिक","Once" to "एकबार")
private val URGENCY_COLOR = listOf(Color.Transparent, Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444))
private val URGENCY_LABEL = listOf("","सामान्य","जरूरी","अति जरूरी")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaryaScreen(viewModel: MainViewModel) {
    val state   by viewModel.karyaState.collectAsState()
    val seed    = LocalSeedTheme.current
    var tab     by remember { mutableIntStateOf(0) }
    var showAdd by remember { mutableStateOf(false) }
    var delId   by remember { mutableStateOf<String?>(null) }
    val snackHost = remember { SnackbarHostState() }

    val today = todayIso()
    val tabs = listOf(
        "आज" to state.tasks.filter { it.deadline == today && !it.isCompleted },
        "सभी" to state.tasks.filter { !it.isCompleted },
        "पूर्ण" to state.tasks.filter { it.isCompleted }
    )
    val filtered = tabs[tab].second
    val donePct  = if (state.tasks.isEmpty()) 0f else state.tasks.count { it.isCompleted }.toFloat() / state.tasks.size

    LaunchedEffect(state.snack) { state.snack?.let { snackHost.showSnackbar(it) } }

    Scaffold(
        topBar = { KutumbTopBar("कार्य") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                containerColor = seed.primary, contentColor = Color.White
            ) { Icon(Icons.Default.Add, null) }
        },
        snackbarHost = { KutumbSnackbarHost(snackHost) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()), contentPadding = PaddingValues(bottom = 100.dp)) {
            // Hero
            item {
                DarkHeroSection(seed) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HeroStatBox("📋", "${state.tasks.size}", "कुल", seed.primary, Modifier.weight(1f))
                        HeroStatBox("✅", "${state.tasks.count { it.isCompleted }}", "पूर्ण", Color(0xFF10B981), Modifier.weight(1f))
                        HeroStatBox("⏳", "${state.tasks.count { !it.isCompleted }}", "शेष", Color(0xFFF59E0B), Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("आज की प्रगति", color = Color(0xFF64748B), fontSize = 11.sp)
                        Text("${(donePct * 100).toInt()}%", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { donePct },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color.White.copy(0.08f)
                    )
                }
                Spacer(Modifier.height(4.dp))
            }

            // Tab row
            item {
                TabRow(
                    selectedTabIndex = tab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[tab]),
                            color = seed.primary, height = 2.5.dp
                        )
                    }
                ) {
                    tabs.forEachIndexed { i, (label, list) ->
                        Tab(selected = tab == i, onClick = { tab = i },
                            text = { Text("$label (${list.size})", fontWeight = if (tab == i) FontWeight.ExtraBold else FontWeight.Medium, fontSize = 13.sp) },
                            selectedContentColor = seed.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Empty
            if (filtered.isEmpty()) item { EmptyState("📭","कोई कार्य नहीं","नया कार्य जोड़ने के लिए + दबाएं") }

            // List
            items(filtered, key = { it.id }) { task ->
                val member = state.members.find { it.id == task.assignedToId }
                Spacer(Modifier.height(8.dp))
                TaskCard(
                    task = task,
                    memberName = member?.name ?: task.assignedToId,
                    memberColorHex = member?.colorHex ?: "#94A3B8",
                    memberShort = member?.shortName ?: "?",
                    urgencyColor = URGENCY_COLOR.getOrElse(task.urgency) { Color(0xFF94A3B8) },
                    urgencyLabel = URGENCY_LABEL.getOrElse(task.urgency) { "" },
                    freqLabel    = FREQ_LABELS[task.frequency] ?: task.frequency,
                    onToggle = { viewModel.toggleTask(task) },
                    onDelete = { delId = task.id },
                    seed = seed
                )
            }
        }
    }

    // Add dialog
    if (showAdd) AddTaskDialog(members = state.members, seed = seed, onDismiss = { showAdd = false },
        onSave = { task -> viewModel.addTask(task); showAdd = false })

    // Delete confirm
    delId?.let { id ->
        AlertDialog(
            onDismissRequest = { delId = null },
            title = { Text("कार्य हटाएं?", fontWeight = FontWeight.ExtraBold) },
            text  = { Text("यह कार्य स्थायी रूप से हट जाएगा।") },
            confirmButton = {
                TextButton(onClick = {
                    state.tasks.find { it.id == id }?.let { viewModel.deleteTask(it) }
                    delId = null
                }) { Text("हटाएं", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { delId = null }) { Text("रद्द") } }
        )
    }
}

@Composable
private fun TaskCard(
    task: Task, memberName: String, memberColorHex: String, memberShort: String,
    urgencyColor: Color, urgencyLabel: String, freqLabel: String,
    onToggle: () -> Unit, onDelete: () -> Unit,
    seed: com.kutumb.app.ui.theme.SeedTheme
) {
    LeftBorderCard(borderColor = urgencyColor, modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = onToggle, Modifier.size(28.dp)) {
                Icon(
                    if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                    null, tint = if (task.isCompleted) Color(0xFF10B981) else Color(0xFF94A3B8)
                )
            }
            Column(Modifier.weight(1f)) {
                Text(task.title, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = MaterialTheme.colorScheme.onSurface)
                if (task.description.isNotBlank())
                    Text(task.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SmallChip(task.category, seed.primary.copy(0.12f), seed.primary)
                    SmallChip(freqLabel, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
                    if (urgencyLabel.isNotEmpty()) SmallChip(urgencyLabel, urgencyColor.copy(0.15f), urgencyColor)
                }
                Spacer(Modifier.height(6.dp))
                Text("⏰ ${task.deadline}", fontSize = 10.sp, color = Color(0xFF475569))
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SmallChip("+${task.points}", Color(0xFF10B981).copy(0.15f), Color(0xFF10B981))
                MemberAvatar(memberShort, memberColorHex, 22.dp, 9.sp)
                IconButton(onClick = onDelete, Modifier.size(22.dp)) {
                    Icon(Icons.Default.Delete, null, Modifier.size(15.dp), tint = Color(0xFFEF4444).copy(0.5f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    members: List<com.kutumb.app.data.model.Member>,
    seed: com.kutumb.app.ui.theme.SeedTheme,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var title      by remember { mutableStateOf("") }
    var desc       by remember { mutableStateOf("") }
    var assignedTo by remember { mutableStateOf(members.firstOrNull()?.id ?: "raj") }
    var category   by remember { mutableStateOf(CATS.first()) }
    var points     by remember { mutableStateOf("30") }
    var frequency  by remember { mutableStateOf(FREQS.first()) }
    var deadline   by remember { mutableStateOf(todayIso()) }
    var urgency    by remember { mutableIntStateOf(1) }
    var titleErr   by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("नया कार्य जोड़ें", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it; titleErr = false },
                    label = { Text("शीर्षक *") }, isError = titleErr,
                    supportingText = if (titleErr) { { Text("शीर्षक आवश्यक है") } } else null,
                    modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = desc, onValueChange = { desc = it },
                    label = { Text("विवरण") }, modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 3)
                DropdownField("सदस्य", members.map { it.name }, members.indexOfFirst { it.id == assignedTo }.coerceAtLeast(0)) {
                    assignedTo = members.getOrNull(it)?.id ?: assignedTo
                }
                DropdownField("श्रेणी", CATS, CATS.indexOf(category)) { category = CATS[it] }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = points, onValueChange = { points = it },
                        label = { Text("अंक") }, modifier = Modifier.weight(1f), singleLine = true)
                    DropdownField("आवृत्ति", FREQS.map { FREQ_LABELS[it] ?: it }, FREQS.indexOf(frequency), Modifier.weight(1f)) { frequency = FREQS[it] }
                }
                OutlinedTextField(value = deadline, onValueChange = { deadline = it },
                    label = { Text("तारीख") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                DropdownField("प्राथमिकता", URGENCY_LABEL.drop(1), urgency - 1) { urgency = it + 1 }
            }
        },
        confirmButton = {
            GradientButton("कार्य सहेजें ✓", seed, onClick = {
                if (title.isBlank()) { titleErr = true; return@GradientButton }
                onSave(Task(id = uuid(), title = title.trim(), description = desc.trim(),
                    assignedToId = assignedTo, points = points.toIntOrNull() ?: 30,
                    deadline = deadline, frequency = frequency,
                    urgency = urgency, category = category, isCompleted = false))
            })
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("रद्द करें") } },
        shape = RoundedCornerShape(24.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(label: String, options: List<String>, selectedIdx: Int, modifier: Modifier = Modifier, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = options.getOrElse(selectedIdx) { options.firstOrNull() ?: "" },
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            singleLine = true
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { i, opt ->
                DropdownMenuItem(text = { Text(opt) }, onClick = { onSelect(i); expanded = false })
            }
        }
    }
}
