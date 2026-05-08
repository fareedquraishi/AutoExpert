package com.autoexpert.app.ui.wallet

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoexpert.app.BuildConfig
import com.autoexpert.app.data.local.dao.PayoutDao
import com.autoexpert.app.data.remote.api.SupabaseApi
import com.autoexpert.app.ui.home.HomeBottomNavBar
import com.autoexpert.app.ui.theme.*
import com.autoexpert.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private val pkrFmt = NumberFormat.getNumberInstance(Locale.US)

data class DayEarning(
    val date: String,
    val earned: Double,
    val litres: Double = 0.0,
    val stationName: String = "",
    val paidDate: String? = null
)

data class WalletUiState(
    val totalEarned: Double = 0.0,
    val totalPaid: Double = 0.0,
    val unpaidBalance: Double = 0.0,
    val days: List<DayEarning> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val payoutDao: PayoutDao,
    private val session: SessionManager,
    private val api: SupabaseApi,
) : ViewModel() {

    private val apiKey     = BuildConfig.SUPABASE_ANON_KEY
    private val authHeader = "Bearer " + apiKey

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState

    init { loadWallet() }

    fun loadWallet() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val baId = session.baId.first() ?: return@launch
            val stationName = session.stationName.first() ?: ""
            try {
                val entries = api.getSaleEntries(
                    baId   = "eq.$baId",
                    date   = "gte.2020-01-01T00:00:00",
                    select = "entry_time,sale_entry_items(commission_earned,qty_litres)",
                    apiKey = apiKey, auth = authHeader
                ).body() ?: emptyList()

                // Group by date
                val dayMap = mutableMapOf<String, Pair<Double, Double>>() // date -> (commission, litres)
                entries.forEach { e ->
                    val d = e.entryTime.take(10)
                    val comm   = e.items?.sumOf { it.commissionEarned } ?: 0.0
                    val litres = e.items?.sumOf { it.qtyLitres } ?: 0.0
                    val cur = dayMap[d] ?: Pair(0.0, 0.0)
                    dayMap[d] = Pair(cur.first + comm, cur.second + litres)
                }

                val payouts = api.getPayouts(
                    baId = "eq.$baId",
                    apiKey = apiKey, auth = authHeader
                ).body() ?: emptyList()

                val totalEarned = dayMap.values.sumOf { it.first }
                val totalPaid   = payouts.sumOf { it.amount }
                val balance     = totalEarned - totalPaid

                // FIFO allocation
                val sortedDays = dayMap.entries.sortedBy { it.key }
                    .map { DayEarning(it.key, it.value.first, it.value.second, stationName) }.toMutableList()
                val sortedPayouts = payouts.sortedBy { it.payoutDate ?: "" }.toMutableList()

                var payIdx = 0
                var payRem = sortedPayouts.getOrNull(0)?.amount ?: 0.0
                for (i in sortedDays.indices) {
                    if (payIdx >= sortedPayouts.size) break
                    var toAllocate = sortedDays[i].earned
                    while (toAllocate > 0.01 && payIdx < sortedPayouts.size) {
                        if (payRem <= 0) {
                            payIdx++
                            payRem = sortedPayouts.getOrNull(payIdx)?.amount ?: 0.0
                            continue
                        }
                        val take = minOf(toAllocate, payRem)
                        toAllocate -= take; payRem -= take
                        if (sortedDays[i].paidDate == null) {
                            sortedDays[i] = sortedDays[i].copy(
                                paidDate = sortedPayouts.getOrNull(payIdx)?.payoutDate?.take(10)
                            )
                        }
                    }
                }

                _uiState.update { it.copy(
                    totalEarned   = totalEarned,
                    totalPaid     = totalPaid,
                    unpaidBalance = balance,
                    days          = sortedDays.reversed(),
                    isLoading     = false,
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

@Composable
fun WalletScreen(
    onBack: () -> Unit,
    onHome: () -> Unit = {},
    onCustomers: () -> Unit = {},
    onChat: () -> Unit = {},
    onProfile: () -> Unit = {},
    vm: WalletViewModel = hiltViewModel()
) {
    val ui by vm.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            HomeBottomNavBar(selected = 3, unreadMessages = 0, onSelect = { i ->
                when (i) { 0 -> onHome(); 1 -> onCustomers(); 2 -> onChat(); 4 -> onProfile() }
            })
        },
        containerColor = Color(0xFFF2F4F5)
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 12.dp)) {

            // Header
            item {
                Box(
                    Modifier.fillMaxWidth()
                        .background(Color(0xFF007273))
                        .padding(16.dp, 16.dp, 16.dp, 20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()) {

                        Text("UNPAID BALANCE", fontSize = 10.sp,
                            color = Color.White.copy(0.6f),
                            letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)

                        Spacer(Modifier.height(6.dp))

                        if (ui.isLoading) {
                            CircularProgressIndicator(color = Color.White,
                                modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Rs ${pkrFmt.format(ui.unpaidBalance.toLong())}",
                                fontSize = 32.sp, fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFBBF24))
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            WalletStat("TOTAL EARNED",
                                "Rs ${pkrFmt.format(ui.totalEarned.toLong())}")
                            Box(Modifier.width(1.dp).height(40.dp)
                                .background(Color.White.copy(0.2f)))
                            WalletStat("TOTAL PAID",
                                "Rs ${pkrFmt.format(ui.totalPaid.toLong())}")
                        }
                    }
                }
            }

            // Section title
            item {
                Text("Daily Commission Log", fontSize = 11.sp,
                    fontWeight = FontWeight.Bold, color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }

            if (ui.days.isEmpty() && !ui.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center) {
                        Text("No commission records yet", fontSize = 13.sp, color = TextSecondary)
                    }
                }
            } else {
                items(ui.days) { day -> DayRow(day) }
            }
        }
    }
}

@Composable
private fun WalletStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 9.sp, color = Color.White.copy(0.55f),
            letterSpacing = 0.5.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun DayRow(day: DayEarning) {
    val isPaid = day.paidDate != null

    val fmt = try {
        val d = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(day.date)
        SimpleDateFormat("dd MMM", Locale.getDefault()).format(d ?: Date())
    } catch (_: Exception) { day.date }

    val paidFmt = try {
        val d = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(day.paidDate ?: "")
        SimpleDateFormat("dd MMM", Locale.getDefault()).format(d ?: Date())
    } catch (_: Exception) { day.paidDate ?: "" }

    Column(
        Modifier.fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 3.dp)
            .background(
                if (isPaid) Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                RoundedCornerShape(10.dp)
            )
            .border(
                1.dp,
                if (isPaid) Color(0xFFA5D6A7) else Color(0xFFFFE082),
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(fmt, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("Rs ${pkrFmt.format(day.earned.toLong())}",
                fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PetronasGreen)
            if (isPaid) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Filled.CheckCircle, null, tint = PetronasGreen,
                        modifier = Modifier.size(13.dp))
                    Text("Paid", fontSize = 11.sp, color = PetronasGreen,
                        fontWeight = FontWeight.Bold)
                }
            } else {
                Text("Unpaid", fontSize = 11.sp, color = Color(0xFFF59E0B),
                    fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(3.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(day.stationName, fontSize = 10.sp, color = TextSecondary)
            Text("%.1fL".format(day.litres), fontSize = 10.sp, color = TextSecondary)
            if (isPaid) {
                Text(paidFmt, fontSize = 10.sp, color = PetronasGreen.copy(0.8f))
            } else {
                Text("?", fontSize = 10.sp, color = Color(0xFFAAAAAA))
            }
        }
    }
}
