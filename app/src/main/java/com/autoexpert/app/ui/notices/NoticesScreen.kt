package com.autoexpert.app.ui.notices

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoexpert.app.data.local.dao.NoticeDao
import com.autoexpert.app.data.local.entity.NoticeEntity
import com.autoexpert.app.ui.components.*
import com.autoexpert.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticesViewModel @Inject constructor(private val noticeDao: NoticeDao) : ViewModel() {
    val notices: StateFlow<List<NoticeEntity>> = noticeDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun markRead(id: String) = viewModelScope.launch { noticeDao.markRead(id) }
    fun markAllRead()        = viewModelScope.launch { noticeDao.markAllRead() }
}

@Composable
fun NoticesScreen(onBack: () -> Unit, vm: NoticesViewModel = hiltViewModel()) {
    val notices by vm.notices.collectAsState()
    Column(Modifier.fillMaxSize().background(BackgroundGray)) {
        AppTopBar(
            title = "Notice Board",
            subtitle = "${notices.count { !it.isRead }} unread",
            onBack = onBack,
            actions = {
                TextButton(onClick = { vm.markAllRead() }) {
                    Text("Mark all read", fontSize = 12.sp, color = PetronasGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
        if (notices.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📭", fontSize = 40.sp)
                    Text("No notices yet", fontSize = 14.sp, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(13.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(notices, key = { it.id }) { notice ->
                    NoticeCard(notice = notice, onClick = { vm.markRead(notice.id) })
                }
            }
        }
    }
}

@Composable
private fun NoticeCard(notice: NoticeEntity, onClick: () -> Unit) {
    Surface(
        color = if (!notice.isRead) Color(0xFFF0FDF4) else Color.White,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (!notice.isRead) PetronasGreen.copy(.25f) else BorderColor),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(Modifier.padding(13.dp), horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top) {
            Box(
                Modifier.size(40.dp).background(
                    if (!notice.isRead) PetronasGreenLight else BackgroundGray,
                    RoundedCornerShape(12.dp)
                ),
                contentAlignment = Alignment.Center
            ) { Text("📢", fontSize = 20.sp) }
            Column(Modifier.weight(1f)) {
                Text(notice.message, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                
            }
            if (!notice.isRead) {
                Box(Modifier.size(8.dp).background(PetronasGreen, CircleShape))
            }
        }
    }
}
