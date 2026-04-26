package com.autoexpert.app.ui.wallet

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
import com.autoexpert.app.data.local.dao.PayoutDao
import com.autoexpert.app.data.local.entity.PayoutEntity
import com.autoexpert.app.ui.components.*
import com.autoexpert.app.ui.theme.*
import com.autoexpert.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val payoutDao: PayoutDao,
    private val session: SessionManager,
) : ViewModel() {
    val payouts: StateFlow<List<PayoutEntity>> = session.baId
        .filterNotNull()
        .flatMapLatest { payoutDao.getByBa(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unpaidBalance: StateFlow<Double> = session.baId
        .filterNotNull()
        .flatMapLatest { baId -> flow { emit(payoutDao.getTotalUnpaid(baId) ?: 0.0) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
}

@Composable
fun WalletScreen(onBack: () -> Unit, vm: WalletViewModel = hiltViewModel()) {
    val payouts by vm.payouts.collectAsState()
    val unpaid  by vm.unpaidBalance.collectAsState()

    Column(Modifier.fillMaxSize().background(BackgroundGray)) {
        AppTopBar("My Wallet", "Commission & Payouts", onBack = onBack)

        Box(
            Modifier.fillMaxWidth()
                .background(HomeHeaderGradient)
                .padding(16.dp, 20.dp, 16.dp, 24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("UNPAID BALANCE", fontSize = 9.sp, color = Color.White.copy(.45f),
                    letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
                Text("₨ ${"%,.0f".format(unpaid)}", fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold, color = Color(0xFFFBBF24),
                    modifier = Modifier.padding(top = 4.dp))
                Text("FIFO — oldest days paid first", fontSize = 10.sp,
                    color = Color.White.copy(.38f), modifier = Modifier.padding(top = 4.dp))
            }
        }

        LazyColumn(contentPadding = PaddingValues(13.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { SectionHeader("DAILY BREAKDOWN", "📅") }
            if (payouts.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("💰", fontSize = 36.sp)
                            Text("No payout records yet", fontSize = 13.sp, color = TextSecondary)
                        }
                    }
                }
            }
            items(payouts) { p ->
                Surface(color = Color.White, shape = RoundedCornerShape(13.dp),
                    border = BorderStroke(1.dp, BorderColor), shadowElevation = 1.dp) {
                    Column(Modifier.fillMaxWidth().padding(13.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(p.earnedDate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Surface(
                                color = if (p.balanceAmount > 0) Color(0xFFFFF9EC) else PetronasGreenLight,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    if (p.balanceAmount > 0) "Unpaid" else "✓ Paid",
                                    fontSize = 9.sp, fontWeight = FontWeight.Bold,
                                    color = if (p.balanceAmount > 0) Color(0xFF92400E) else PetronasGreenDark,
                                    modifier = Modifier.padding(7.dp, 3.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            WalletCell("Earned",    "₨${"%,.0f".format(p.earnedAmount)}",    Modifier.weight(1f))
                            WalletCell("Allocated", "₨${"%,.0f".format(p.allocatedAmount)}", Modifier.weight(1f))
                            WalletCell("Balance",   "₨${"%,.0f".format(p.balanceAmount)}",   Modifier.weight(1f),
                                valueColor = if (p.balanceAmount > 0) AccentRed else PetronasGreen)
                        }
                        p.paymentDate?.let {
                            Text("Paid on $it", fontSize = 10.sp, color = TextDim, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletCell(label: String, value: String, modifier: Modifier, valueColor: Color = TextPrimary) {
    Column(modifier.background(BackgroundGray, RoundedCornerShape(8.dp)).padding(8.dp, 6.dp)) {
        Text(label, fontSize = 9.sp, color = TextDim, fontWeight = FontWeight.Bold, letterSpacing = .5.sp)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor,
            modifier = Modifier.padding(top = 2.dp))
    }
}
