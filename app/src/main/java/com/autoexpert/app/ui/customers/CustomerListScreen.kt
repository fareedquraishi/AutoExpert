package com.autoexpert.app.ui.customers

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
import com.autoexpert.app.data.local.dao.SaleEntryQueueDao
import com.autoexpert.app.data.local.entity.SaleEntryQueueEntity
import com.autoexpert.app.ui.components.*
import com.autoexpert.app.ui.theme.*
import com.autoexpert.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CustomerListViewModel @Inject constructor(
    private val saleDao: SaleEntryQueueDao,
    private val session: SessionManager,
) : ViewModel() {
    val entries: StateFlow<List<SaleEntryQueueEntity>> = session.baId
        .filterNotNull()
        .flatMapLatest { saleDao.getByBa(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@Composable
fun CustomerListScreen(
    onBack: () -> Unit,
    onNewCustomer: () -> Unit,
    vm: CustomerListViewModel = hiltViewModel()
) {
    val entries by vm.entries.collectAsState()
    var filter by remember { mutableStateOf("All") }
    val tabs = listOf("All", "Sold", "No Sale", "Existing", "✦ App")

    val filtered = entries.filter { e -> when (filter) {
        "Sold"     -> e.totalLitres > 0
        "No Sale"  -> e.totalLitres == 0.0 && !e.isRepeat
        "Existing" -> e.isRepeat && e.totalLitres == 0.0
        "✦ App"   -> e.isApplicator
        else       -> true
    }}

    Column(Modifier.fillMaxSize().background(BackgroundGray)) {
        AppTopBar("Customers", "${entries.size} total", onBack = onBack)

        ScrollableTabRow(
            selectedTabIndex = tabs.indexOf(filter).coerceAtLeast(0),
            containerColor = Color.White,
            contentColor = PetronasGreen,
            edgePadding = 12.dp,
            divider = { HorizontalDivider(color = BorderColor) }
        ) {
            tabs.forEach { tab ->
                Tab(selected = filter == tab, onClick = { filter = tab },
                    selectedContentColor = PetronasGreen,
                    unselectedContentColor = TextSecondary) {
                    Text(tab, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp, 10.dp))
                }
            }
        }

        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📋", fontSize = 36.sp)
                    Text("No entries in this category", fontSize = 13.sp, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(13.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                items(filtered, key = { it.localId }) { entry ->
                    CustomerEntryCard(entry)
                }
            }
        }
    }
}

@Composable
private fun CustomerEntryCard(entry: SaleEntryQueueEntity) {
    Surface(color = Color.White, shape = RoundedCornerShape(13.dp),
        border = BorderStroke(1.dp, BorderColor), shadowElevation = 1.dp) {
        Row(Modifier.fillMaxWidth().padding(11.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            val initial = entry.customerName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            Box(
                Modifier.size(38.dp).background(
                    if (entry.totalLitres > 0) GreenGradient
                    else Brush.linearGradient(listOf(Color(0xFF6B7280), Color(0xFF4B5563))),
                    RoundedCornerShape(10.dp)
                ),
                contentAlignment = Alignment.Center
            ) { Text(initial, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White) }
            Column(Modifier.weight(1f)) {
                Text(entry.customerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(buildString {
                    append(entry.vehicleTypeName ?: "")
                    if (entry.totalLitres > 0) append(" · ${entry.totalLitres}L") else append(" · No sale")
                    if (entry.isApplicator) append(" · ✦")
                }, fontSize = 10.sp, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val (bType, bText) = when {
                    !entry.isRepeat && entry.totalLitres > 0 -> BadgeType.GREEN  to "Conquest"
                    entry.isRepeat  && entry.totalLitres > 0 -> BadgeType.BLUE   to "Repeat"
                    entry.isRepeat                           -> BadgeType.AMBER  to "Existing"
                    entry.isApplicator                       -> BadgeType.PURPLE to "✦ App"
                    else                                     -> BadgeType.GRAY   to "Prospect"
                }
                StatusBadge(bText, bType)
                if (entry.syncStatus == "pending") {
                    Surface(color = Color(0xFFFFF9EC), shape = RoundedCornerShape(6.dp)) {
                        Text("⏳ Pending", fontSize = 8.sp, color = Color(0xFF92400E),
                            fontWeight = FontWeight.Bold, modifier = Modifier.padding(5.dp, 2.dp))
                    }
                }
            }
        }
    }
}
