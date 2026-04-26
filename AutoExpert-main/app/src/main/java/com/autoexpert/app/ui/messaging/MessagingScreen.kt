package com.autoexpert.app.ui.messaging

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoexpert.app.BuildConfig
import com.autoexpert.app.data.local.dao.MessageDao
import com.autoexpert.app.data.local.entity.MessageEntity
import com.autoexpert.app.data.remote.api.SupabaseApi
import com.autoexpert.app.ui.components.DarkGradient
import com.autoexpert.app.ui.components.HomeHeaderGradient
import com.autoexpert.app.ui.theme.*
import com.autoexpert.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MessagingViewModel @Inject constructor(
    private val messageDao: MessageDao,
    private val api: SupabaseApi,
    private val session: SessionManager,
) : ViewModel() {

    val messages: StateFlow<List<MessageEntity>> = messageDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val baId: StateFlow<String?> = session.baId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val draftText = MutableStateFlow("")

    private val apiKey = BuildConfig.SUPABASE_ANON_KEY
    private val auth = "Bearer $apiKey"

    init {
        viewModelScope.launch {
            markRead()
            syncMessages()
        }
    }

    private suspend fun syncMessages() {
        val id = session.baId.first() ?: return
        try {
            api.getMessages(
                filter = "(sender_id.eq.$id,receiver_id.eq.$id)",
                apiKey = apiKey, auth = auth
            ).body()?.let { msgs ->
                val entities = msgs.map { m ->
                    MessageEntity(
                        id = m.id, senderId = m.senderId, senderName = m.senderName,
                        receiverId = m.receiverId, body = m.body,
                        createdAt = System.currentTimeMillis(),
                        isRead = m.isRead, isOutgoing = m.senderId == id
                    )
                }
                messageDao.upsertAll(entities)
                markRead()
            }
        } catch (_: Exception) {}
    }

    private suspend fun markRead() {
        val id = baId.first() ?: return
        messageDao.markAllRead(id)
        try {
            api.markMessagesRead(
                baId = "eq.$id",
                body = mapOf("is_read" to true),
                apiKey = apiKey, auth = auth
            )
        } catch (_: Exception) {}
    }

    fun sendMessage() {
        val text = draftText.value.trim()
        if (text.isEmpty()) return
        draftText.value = ""
        viewModelScope.launch {
            val baId = session.baId.first() ?: return@launch
            val baName = session.baName.first() ?: "BA"
            val localMsg = MessageEntity(
                id = UUID.randomUUID().toString(),
                senderId = baId, senderName = baName,
                receiverId = "admin", body = text,
                createdAt = System.currentTimeMillis(),
                isRead = true, isOutgoing = true
            )
            messageDao.insert(localMsg)
            try {
                api.postMessage(
                    body = mapOf("sender_id" to baId, "sender_name" to baName,
                        "receiver_id" to "admin", "body" to text),
                    apiKey = apiKey, auth = auth
                )
            } catch (_: Exception) {}
        }
    }
}

private fun Long.toTimeString(): String =
    SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(this))

@Composable
fun MessagingScreen(onBack: () -> Unit, vm: MessagingViewModel = hiltViewModel()) {
    val messages by vm.messages.collectAsState()
    val baId by vm.baId.collectAsState()
    val draft by vm.draftText.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Column(Modifier.fillMaxSize().background(BackgroundGray)) {

        // Dark top bar
        Box(Modifier.fillMaxWidth().background(HomeHeaderGradient)) {
            Row(
                Modifier.fillMaxWidth().statusBarsPadding().padding(14.dp, 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                    Box(
                        Modifier.fillMaxSize()
                            .background(Color.White.copy(.09f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
                // Admin avatar
                Box(
                    Modifier.size(38.dp)
                        .background(PetronasGreen.copy(.18f), RoundedCornerShape(11.dp))
                        .border(1.dp, PetronasGreen.copy(.28f), RoundedCornerShape(11.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("👨‍💼", fontSize = 18.sp) }
                Column(Modifier.weight(1f)) {
                    Text("Admin · Head Office", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(Modifier.size(7.dp).background(PetronasGreen, CircleShape))
                        Text("Online", fontSize = 10.sp, color = PetronasGreen.copy(.85f), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                when {
                    msg.body.startsWith("💰") || msg.body.startsWith("✅") || msg.body.startsWith("❌") ->
                        SystemNotifBubble(msg.body)
                    msg.isOutgoing ->
                        OutgoingBubble(msg)
                    else ->
                        IncomingBubble(msg)
                }
            }
        }

        // Input row
        Surface(color = Color.White, shadowElevation = 4.dp) {
            Row(
                Modifier.fillMaxWidth().navigationBarsPadding().padding(11.dp, 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📎", fontSize = 20.sp, color = TextSecondary)
                OutlinedTextField(
                    value = draft,
                    onValueChange = { vm.draftText.value = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message…", color = TextDim, fontSize = 13.sp) },
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = PetronasGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedContainerColor= BackgroundGray,
                        unfocusedContainerColor = BackgroundGray,
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                    maxLines = 3
                )
                Box(
                    Modifier.size(40.dp)
                        .background(GreenGradient, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { vm.sendMessage() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun IncomingBubble(msg: MessageEntity) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            Modifier.size(28.dp)
                .background(Color(0xFFE5E7EB), RoundedCornerShape(9.dp))
                .padding(end = 7.dp),
            contentAlignment = Alignment.Center
        ) { Text("👨‍💼", fontSize = 13.sp) }
        Spacer(Modifier.width(7.dp))
        Column {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 3.dp),
                shadowElevation = 1.dp
            ) {
                Text(msg.body, fontSize = 13.sp, color = TextPrimary, lineHeight = 18.sp,
                    modifier = Modifier.widthIn(max = 260.dp).padding(12.dp, 9.dp))
            }
            Text(msg.createdAt.toTimeString(), fontSize = 9.sp, color = TextDim, modifier = Modifier.padding(top = 3.dp))
        }
    }
}

@Composable
private fun OutgoingBubble(msg: MessageEntity) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 3.dp))
                    .background(GreenGradient)
                    .padding(12.dp, 9.dp)
            ) {
                Text(msg.body, fontSize = 13.sp, color = Color.White, lineHeight = 18.sp)
            }
            Text(msg.createdAt.toTimeString(), fontSize = 9.sp, color = TextDim, modifier = Modifier.padding(top = 3.dp))
        }
    }
}

@Composable
private fun SystemNotifBubble(text: String) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Surface(
            color = Color(0xFFFFF9EC),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color(0xFFFDE68A))
        ) {
            Text(text, fontSize = 11.sp, color = Color(0xFF92400E), fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
        }
    }
}
