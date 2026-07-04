package com.kutumb.app.ui.screens.samvaad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutumb.app.data.model.ChatMessage
import com.kutumb.app.ui.components.KutumbTopBar
import com.kutumb.app.ui.components.MemberAvatar
import com.kutumb.app.ui.theme.LocalSeedTheme
import com.kutumb.app.ui.theme.gradientBrush
import com.kutumb.app.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun SamvaadScreen(viewModel: MainViewModel) {
    val state    by viewModel.samvaadState.collectAsState()
    val seed     = LocalSeedTheme.current
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope     = rememberCoroutineScope()

    // Auto-scroll to bottom when messages change
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) listState.animateScrollToItem(state.messages.size - 1)
    }

    val currentUser = state.members.firstOrNull { it.role == "admin" }
        ?: state.members.firstOrNull()

    Scaffold(
        topBar = {
            KutumbTopBar(
                title = "संवाद",
                actions = {
                    // Online member avatars
                    Row(horizontalArrangement = Arrangement.spacedBy((-4).dp), modifier = Modifier.padding(end = 4.dp)) {
                        state.members.take(3).forEach { m ->
                            MemberAvatar(m.shortName, m.colorHex, 24.dp, 9.sp)
                        }
                    }
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = Color(0xFF475569)) }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            // Online indicator strip
            Row(
                Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(Modifier.size(8.dp).background(Color(0xFF10B981), CircleShape))
                Text("${state.members.size} सदस्य ऑनलाइन", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            HorizontalDivider()

            // Messages
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("आज", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                }
                items(state.messages, key = { it.id }) { msg ->
                    MessageBubble(msg = msg, seed = seed)
                }
            }

            HorizontalDivider()

            // Input bar
            Row(
                Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = {}, Modifier.size(36.dp)) {
                    Icon(Icons.Default.AttachFile, null, tint = Color(0xFF64748B))
                }
                IconButton(onClick = {}, Modifier.size(36.dp)) {
                    Icon(Icons.Default.Image, null, tint = Color(0xFF64748B))
                }
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("संदेश लिखें...", fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedBorderColor    = Color.Transparent,
                        focusedBorderColor      = Color.Transparent
                    ),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (inputText.isNotBlank() && currentUser != null) {
                            viewModel.sendMessage(inputText.trim(), currentUser)
                            inputText = ""
                            scope.launch { if (state.messages.isNotEmpty()) listState.animateScrollToItem(state.messages.size) }
                        }
                    })
                )
                Box(
                    Modifier.size(40.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank()) seed.gradientBrush else androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF64748B), Color(0xFF64748B))))
                        .clickable {
                            if (inputText.isNotBlank() && currentUser != null) {
                                viewModel.sendMessage(inputText.trim(), currentUser)
                                inputText = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessage, seed: com.kutumb.app.ui.theme.SeedTheme) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!msg.isMe) {
            MemberAvatar(msg.senderShortName, msg.senderColorHex, 26.dp, 10.sp)
            Spacer(Modifier.width(6.dp))
        }
        Column(
            horizontalAlignment = if (msg.isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 270.dp)
        ) {
            if (!msg.isMe) {
                Text(msg.senderName, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    color = Color(android.graphics.Color.parseColor(msg.senderColorHex)),
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(
                        topStart = 18.dp, topEnd = 18.dp,
                        bottomStart = if (msg.isMe) 18.dp else 4.dp,
                        bottomEnd   = if (msg.isMe) 4.dp   else 18.dp
                    ))
                    .background(
                        if (msg.isMe) seed.gradientBrush
                        else androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    Text(msg.text, fontSize = 14.sp, color = if (msg.isMe) Color.White else MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
                    val ts = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(msg.timestamp))
                    Text(ts, fontSize = 9.sp, color = if (msg.isMe) Color.White.copy(0.6f) else Color(0xFF94A3B8),
                        textAlign = if (msg.isMe) TextAlign.End else TextAlign.Start,
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp))
                }
            }
        }
        if (msg.isMe) {
            Spacer(Modifier.width(6.dp))
            MemberAvatar(msg.senderShortName, msg.senderColorHex, 26.dp, 10.sp)
        }
    }
}

private fun androidx.compose.ui.Modifier.clickable(onClick: () -> Unit) = this.then(Modifier.clickable(onClick = onClick))
