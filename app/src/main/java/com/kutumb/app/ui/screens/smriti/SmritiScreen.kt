package com.kutumb.app.ui.screens.smriti

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kutumb.app.data.model.Memory
import com.kutumb.app.ui.components.*
import com.kutumb.app.ui.theme.LocalSeedTheme
import com.kutumb.app.ui.viewmodel.MainViewModel
import com.kutumb.app.ui.viewmodel.uuid

private val EMOJIS = listOf("🎂","🏞️","🎉","🌅","🍽️","🎓","🌺","🎪","🏖️","🎵","🕯️","🌿","🎠","🎗️","🏡","💐")
private val BG_COLORS = listOf("#FFF7ED","#F0FDF4","#F5F3FF","#EFF6FF","#FFF1F2","#ECFDF5","#FDF4FF","#FEFCE8")

@Composable
fun SmritiScreen(viewModel: MainViewModel) {
    val state    by viewModel.smritiState.collectAsState()
    val seed     = LocalSeedTheme.current
    var selected by remember { mutableStateOf<Memory?>(null) }
    var showAdd  by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            KutumbTopBar(
                title = "स्मृति",
                actions = {
                    AssistChip(onClick = {}, label = { Text("${state.memories.size} यादें", fontSize = 11.sp) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, labelColor = MaterialTheme.colorScheme.onSurfaceVariant))
                    Spacer(Modifier.width(8.dp))
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }, containerColor = seed.primary, contentColor = Color.White) {
                Icon(Icons.Default.AddAPhoto, null)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            // Month header
            Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text("जून 2025", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("परिवार की अमूल्य यादें", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (state.memories.isEmpty()) {
                EmptyState("📷","कोई यादें नहीं","पहली याद जोड़ने के लिए 📷 दबाएं")
            } else {
                // 2-column staggered grid
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp,
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    itemsIndexed(state.memories) { idx, memory ->
                        val isTall = idx % 3 == 0
                        MemoryCard(memory = memory, isTall = isTall,
                            onLikeToggle = { viewModel.toggleMemoryLike(memory) },
                            onClick = { selected = memory })
                    }
                }
            }
        }
    }

    // Detail dialog
    selected?.let { mem ->
        Dialog(onDismissRequest = { selected = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Card(Modifier.fillMaxWidth(0.9f), shape = RoundedCornerShape(24.dp)) {
                Column {
                    Box(
                        Modifier.fillMaxWidth().height(220.dp)
                            .background(Color(android.graphics.Color.parseColor(mem.bgColorHex))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(mem.emoji, fontSize = 80.sp)
                        IconButton(onClick = { selected = null },
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                            Icon(Icons.Default.Close, null, tint = Color.White,
                                modifier = Modifier.background(Color.Black.copy(0.4f), RoundedCornerShape(50)))
                        }
                    }
                    Column(Modifier.padding(20.dp)) {
                        Text(mem.caption, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, lineHeight = 24.sp)
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MemberAvatar(mem.uploaderName.first().toString(), mem.uploaderColorHex, 34.dp, 13.sp)
                            Column {
                                Text(mem.uploaderName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(mem.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { viewModel.toggleMemoryLike(mem); selected = mem.copy(isLiked = !mem.isLiked) }) {
                                Icon(
                                    if (mem.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    null, tint = if (mem.isLiked) Color(0xFFEF4444) else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add memory dialog
    if (showAdd) AddMemoryDialog(seed = seed, onDismiss = { showAdd = false }, onSave = { viewModel.addMemory(it); showAdd = false })
}

@Composable
private fun MemoryCard(memory: Memory, isTall: Boolean, onLikeToggle: () -> Unit, onClick: () -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(20.dp)) {
        Column {
            Box(
                Modifier.fillMaxWidth().height(if (isTall) 155.dp else 108.dp)
                    .background(Color(android.graphics.Color.parseColor(memory.bgColorHex))),
                contentAlignment = Alignment.Center
            ) { Text(memory.emoji, fontSize = if (isTall) 56.sp else 40.sp) }
            Column(Modifier.background(MaterialTheme.colorScheme.surface).padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)) {
                Text(memory.caption, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(memory.date, fontSize = 10.sp, color = Color(0xFF64748B))
                    IconButton(onClick = onLikeToggle, Modifier.size(24.dp)) {
                        Icon(
                            if (memory.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            null, Modifier.size(14.dp), tint = if (memory.isLiked) Color(0xFFEF4444) else Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddMemoryDialog(seed: com.kutumb.app.ui.theme.SeedTheme, onDismiss: () -> Unit, onSave: (Memory) -> Unit) {
    var emoji    by remember { mutableStateOf("🎂") }
    var caption  by remember { mutableStateOf("") }
    var colorIdx by remember { mutableIntStateOf(0) }
    var capErr   by remember { mutableStateOf(false) }

    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("नई स्मृति जोड़ें", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("इमोजी चुनें", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = GridCells.Fixed(8), modifier = Modifier.height(88.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(EMOJIS) { e ->
                        Box(
                            Modifier.size(32.dp)
                                .background(if (emoji == e) seed.primary.copy(0.2f) else MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .border(if (emoji == e) 2.dp else 0.dp, seed.primary, RoundedCornerShape(8.dp))
                                .clickable { emoji = e },
                            contentAlignment = Alignment.Center
                        ) { Text(e, fontSize = 16.sp) }
                    }
                }
                OutlinedTextField(value = caption, onValueChange = { caption = it; capErr = false },
                    label = { Text("विवरण *") }, isError = capErr,
                    supportingText = if (capErr) { { Text("विवरण आवश्यक है") } } else null,
                    modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 3)
                Text("पृष्ठभूमि रंग", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BG_COLORS.forEachIndexed { i, hex ->
                        Box(
                            Modifier.size(28.dp).clip(RoundedCornerShape(14.dp))
                                .background(Color(android.graphics.Color.parseColor(hex)))
                                .then(if (colorIdx == i) Modifier.border(2.dp, seed.primary, RoundedCornerShape(14.dp)) else Modifier)
                                .clickable { colorIdx = i }
                        )
                    }
                }
            }
        },
        confirmButton = {
            GradientButton("सहेजें ✓", seed, onClick = {
                if (caption.isBlank()) { capErr = true; return@GradientButton }
                onSave(Memory(id = uuid(), emoji = emoji, caption = caption.trim(),
                    uploaderId = "raj", uploaderName = "राज", uploaderColorHex = "#FF6B35",
                    date = "आज", bgColorHex = BG_COLORS[colorIdx]))
            })
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("रद्द करें") } },
        shape = RoundedCornerShape(24.dp)
    )
}
