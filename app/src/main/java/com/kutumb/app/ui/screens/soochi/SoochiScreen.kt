package com.kutumb.app.ui.screens.soochi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutumb.app.data.model.GroceryItem
import com.kutumb.app.data.model.GroceryList
import com.kutumb.app.ui.components.*
import com.kutumb.app.ui.theme.LocalSeedTheme
import com.kutumb.app.ui.viewmodel.MainViewModel
import com.kutumb.app.ui.viewmodel.uuid

private val LIST_EMOJIS = listOf("🛒","🍎","🛋️","🎯","💪","📝","✈️","🎁","📚","🏠")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoochiScreen(viewModel: MainViewModel) {
    val state    by viewModel.soochiState.collectAsState()
    val seed     = LocalSeedTheme.current
    var activeId by remember { mutableStateOf<String?>(null) }
    var showAddItem by remember { mutableStateOf(false) }
    var showAddList by remember { mutableStateOf(false) }
    val snack       = remember { SnackbarHostState() }

    val activeList  = state.lists.find { it.id == activeId }
    val activeItems = state.items.filter { it.listId == activeId }
    val donePct     = if (activeItems.isEmpty()) 0f else activeItems.count { it.isDone }.toFloat() / activeItems.size

    Scaffold(
        topBar = {
            KutumbTopBar(
                title = activeList?.name ?: "सूची",
                onBack = if (activeId != null) { { activeId = null } } else null,
                actions = {
                    if (activeId != null) {
                        AssistChip(onClick = {}, label = { Text("${(donePct*100).toInt()}% पूर्ण", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF10B981).copy(0.2f), labelColor = Color(0xFF10B981)))
                        Spacer(Modifier.width(8.dp))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (activeId != null) showAddItem = true else showAddList = true },
                containerColor = seed.primary, contentColor = Color.White
            ) { Icon(Icons.Default.Add, null) }
        },
        snackbarHost = { KutumbSnackbarHost(snack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            if (activeId == null) {
                // Dashboard grid
                Text("आपकी सभी साझा सूचियाँ", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(state.lists) { list ->
                        val listItems = state.items.filter { it.listId == list.id }
                        ListFolderCard(list, listItems) { activeId = list.id }
                    }
                    // Add new list tile
                    item {
                        Box(
                            Modifier.fillMaxWidth().aspectRatio(0.9f)
                                .border(2.dp, MaterialTheme.colorScheme.outline.copy(0.4f), RoundedCornerShape(20.dp))
                                .clickable { showAddList = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("➕", fontSize = 24.sp)
                                Text("नई सूची", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                // Detail view
                LinearProgressIndicator(
                    progress = { donePct },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = Color(0xFF10B981), trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                if (activeItems.isEmpty()) {
                    EmptyState("🛒","सूची खाली है","वस्तु जोड़ने के लिए + दबाएं")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)
                    ) {
                        item {
                            ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(20.dp)) {
                                activeItems.sortedBy { it.isDone }.forEachIndexed { i, item ->
                                    Row(
                                        Modifier.fillMaxWidth()
                                            .alpha(if (item.isDone) 0.45f else 1f)
                                            .padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Checkbox(checked = item.isDone, onCheckedChange = { viewModel.toggleGroceryItem(item) },
                                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981), uncheckedColor = Color(0xFF64748B)))
                                        Column(Modifier.weight(1f)) {
                                            Text(item.text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                                                textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None)
                                            Text(item.quantity, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            MemberAvatar(item.addedByName.first().toString(), item.addedByColorHex, 22.dp, 9.sp)
                                            Text(item.addedByName, fontSize = 10.sp, color = Color(0xFF64748B))
                                        }
                                    }
                                    if (i < activeItems.size - 1) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddItem) AddItemDialog(members = emptyList(), seed = seed, listId = activeId ?: "",
        onDismiss = { showAddItem = false }, onSave = { viewModel.addGroceryItem(it); showAddItem = false })

    if (showAddList) AddListDialog(seed = seed, onDismiss = { showAddList = false },
        onSave = { viewModel.addGroceryList(it); showAddList = false })
}

@Composable
private fun ListFolderCard(list: GroceryList, items: List<GroceryItem>, onClick: () -> Unit) {
    val done = items.count { it.isDone }
    val pct  = if (items.isEmpty()) 0f else done.toFloat() / items.size
    val color = Color(android.graphics.Color.parseColor(list.colorHex))
    ElevatedCard(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(14.dp)) {
            Text(list.emoji, fontSize = 30.sp)
            Spacer(Modifier.height(8.dp))
            Text(list.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$done/${items.size} पूर्ण", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${(pct*100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            }
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(progress = { pct }, modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                color = color, trackColor = MaterialTheme.colorScheme.surfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddItemDialog(members: List<com.kutumb.app.data.model.Member>, seed: com.kutumb.app.ui.theme.SeedTheme, listId: String, onDismiss: () -> Unit, onSave: (GroceryItem) -> Unit) {
    var text     by remember { mutableStateOf("") }
    var qty      by remember { mutableStateOf("") }
    var byName   by remember { mutableStateOf("राज") }
    var byColor  by remember { mutableStateOf("#FF6B35") }
    var textErr  by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("वस्तु जोड़ें", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = text, onValueChange = { text = it; textErr = false }, label = { Text("वस्तु का नाम *") },
                    isError = textErr, supportingText = if (textErr) { { Text("नाम आवश्यक है") } } else null, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("मात्रा") }, placeholder = { Text("जैसे: 2 kg") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            GradientButton("जोड़ें ✓", seed, onClick = {
                if (text.isBlank()) { textErr = true; return@GradientButton }
                onSave(GroceryItem(id = uuid(), listId = listId, text = text.trim(), quantity = qty.ifBlank { "1" },
                    isDone = false, addedByName = byName, addedByColorHex = byColor))
            })
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("रद्द") } },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun AddListDialog(seed: com.kutumb.app.ui.theme.SeedTheme, onDismiss: () -> Unit, onSave: (GroceryList) -> Unit) {
    var name     by remember { mutableStateOf("") }
    var emoji    by remember { mutableStateOf("🛒") }
    var nameErr  by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("नई सूची बनाएं", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it; nameErr = false }, label = { Text("सूची का नाम *") },
                    isError = nameErr, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Text("इमोजी चुनें", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = GridCells.Fixed(5), modifier = Modifier.height(90.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(LIST_EMOJIS) { e ->
                        Box(
                            Modifier.size(36.dp)
                                .background(if (emoji == e) seed.primary.copy(0.2f) else MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                                .border(if (emoji == e) 2.dp else 0.dp, seed.primary, RoundedCornerShape(10.dp))
                                .clickable { emoji = e },
                            contentAlignment = Alignment.Center
                        ) { Text(e, fontSize = 18.sp) }
                    }
                }
            }
        },
        confirmButton = {
            GradientButton("बनाएं ✓", seed, onClick = {
                if (name.isBlank()) { nameErr = true; return@GradientButton }
                onSave(GroceryList(id = uuid(), name = name.trim(), emoji = emoji,
                    colorHex = seed.primary.toString(), bgColorHex = "#F1F5F9"))
            })
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("रद्द") } },
        shape = RoundedCornerShape(24.dp)
    )
}
