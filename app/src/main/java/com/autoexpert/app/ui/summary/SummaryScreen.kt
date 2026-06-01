package com.autoexpert.app.ui.summary

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoexpert.app.data.local.dao.PayoutDao
import com.autoexpert.app.data.local.dao.SaleEntryQueueDao
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

data class SummaryUiState(
    val baName: String = "",
    val stationName: String = "",
    val todayReach: Int = 0,
    val reachTarget: Double = 100.0,
    val todayLitres: Double = 0.0,
    val litresTarget: Double = 100.0,
    val todayCommission: Double = 0.0,
    val unpaidBalance: Double = 0.0,
    val conquest: Int = 0,
    val repeat: Int = 0,
    val existing: Int = 0,
    val prospect: Int = 0,
    val vehicleCounts: Map<String, Int> = emptyMap(),
    val productSales: Map<String, Double> = emptyMap(),
    val packSales: Map<String, Int> = emptyMap(),
    val packSizes: Map<String, Double> = emptyMap(),
    val applicatorCount: Int = 0,
    val yesterdayReach: Int = 0,
    val yesterdayLitres: Double = 0.0,
    val yesterdayCommission: Double = 0.0,
    val isLoading: Boolean = true,
)

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val saleDao: SaleEntryQueueDao,
    private val payoutDao: PayoutDao,
    private val session: SessionManager,
    private val skuDao: com.autoexpert.app.data.local.dao.SkuDao,
) : ViewModel() {

    private val _state = MutableStateFlow(SummaryUiState())
    val state: StateFlow<SummaryUiState> = _state

    init { load() }

    fun load() {
        viewModelScope.launch {
            val baId       = session.baId.first() ?: return@launch
            val baName     = session.baName.first() ?: ""
            val stName     = session.stationName.first() ?: ""
            val today      = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val cal        = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterday  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

            val todayEntries     = saleDao.getByBaAndDate(baId, today).first()
            val yesterdayEntries = saleDao.getByBaAndDate(baId, yesterday).first()
            val payouts          = payoutDao.getByBa(baId).first()

            val totalEarned = todayEntries.sumOf { it.totalCommission }
            val totalPaid   = payouts.sumOf { it.amount }
            val allEarned   = saleDao.getByBa(baId).first().sumOf { it.totalCommission }
            val unpaid      = allEarned - totalPaid

            val productMap = mutableMapOf<String, Double>()
            val packMap = mutableMapOf<String, Int>()
            val packSizeMap = mutableMapOf<String, Double>()
            val skuList = skuDao.getAllActiveOnce()
            todayEntries.forEach { e ->
                try {
                    val json = e.itemsJson
                    if (json != "[]" && json.isNotEmpty()) {
                        val items = json.removeSurrounding("[", "]").split("},")
                        items.forEach { item ->
                            val name = Regex("""skuName":"([^"]+)""").find(item)?.groupValues?.get(1)
                            val qty  = Regex("""qty":([0-9.]+)""").find(item)?.groupValues?.get(1)?.toDoubleOrNull()
                            if (name != null && name.isNotEmpty() && qty != null) {
                                productMap[name] = (productMap[name] ?: 0.0) + qty
                            }
                        }
                    }
                } catch (_: Exception) {}
            }

            val vehicleMap = mutableMapOf<String, Int>()
            todayEntries.forEach { e ->
                val v = e.vehicleTypeName?.ifEmpty { null } ?: "Unknown"
                vehicleMap[v] = (vehicleMap[v] ?: 0) + 1
            }

            _state.update { it.copy(
                baName           = baName,
                stationName      = stName,
                todayReach       = todayEntries.size,
                todayLitres      = todayEntries.sumOf { it.totalLitres },
                todayCommission  = totalEarned,
                unpaidBalance    = unpaid,
                conquest         = todayEntries.count { !it.isRepeat && it.totalLitres > 0 },
                repeat           = todayEntries.count { it.isRepeat && it.totalLitres > 0 },
                existing         = todayEntries.count { it.isRepeat && it.totalLitres == 0.0 },
                prospect         = todayEntries.count { !it.isRepeat && it.totalLitres == 0.0 },
                vehicleCounts    = vehicleMap,
                productSales     = productMap,
                applicatorCount  = todayEntries.count { it.isApplicator },
                yesterdayReach   = yesterdayEntries.size,
                yesterdayLitres  = yesterdayEntries.sumOf { it.totalLitres },
                yesterdayCommission = yesterdayEntries.sumOf { it.totalCommission },
                isLoading        = false,
            )}
        }
    }

    fun buildReportText(state: SummaryUiState): String {
        val sdf  = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
        val date = sdf.format(Date())
        val now  = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val div  = "___________________________"

        val products = if (state.productSales.isEmpty()) {
            "  - No products sold"
        } else {
            state.productSales.entries.joinToString("\n") { entry ->
                val packs = state.packSales[entry.key] ?: 0
                val volL  = state.packSizes[entry.key] ?: 0.0
                val volStr = if (volL > 0) " (" + "%.1f".format(volL) + "L)" else ""
                if (packs > 0) {
                    val packsStr = if (packs < 10) "0$packs" else "$packs"
                    "  - $packsStr X " + entry.key + volStr
                } else {
                    "  - " + entry.key + ": " + "%.1f".format(entry.value) + "L"
                }
            }
        }

        val vehicles = if (state.vehicleCounts.isEmpty()) {
            "None"
        } else {
            state.vehicleCounts.entries.joinToString(" | ") { it.key + ": " + it.value }
        }

        val sb = StringBuilder()
        sb.appendLine("*Daily Summary*")
        sb.appendLine("_" + state.baName + " | " + state.stationName + "_")
        sb.appendLine("_" + date + " | Shift: 10:00 AM - " + now + "_")
        sb.appendLine()
        sb.appendLine(div)
        sb.appendLine("*Performance*")
        sb.appendLine(div)
        sb.appendLine("*Reach:* _" + state.todayReach + " customers_")
        sb.appendLine("*Litres:* _" + "%.1f".format(state.todayLitres) + "L_")
        sb.appendLine("*Commission:* _Rs " + pkrFmt.format(state.todayCommission.toLong()) + "_")
        sb.appendLine()
        sb.appendLine(div)
        sb.appendLine("*Customer Breakdown*")
        sb.appendLine(div)
        sb.appendLine("_Conquest: " + state.conquest + " | Repeat: " + state.repeat + "_")
        sb.appendLine("_Existing: " + state.existing + " | Prospect: " + state.prospect + "_")
        sb.appendLine()
        sb.appendLine(div)
        sb.appendLine("*By Vehicle*")
        sb.appendLine(div)
        sb.appendLine("_" + vehicles + "_")
        sb.appendLine()
        sb.appendLine(div)
        sb.appendLine("*Products Sold*")
        sb.appendLine(div)
        sb.appendLine(products)
        sb.appendLine(div)
        sb.appendLine("_*Prepared by Fintectual Pvt Ltd*_")
        sb.appendLine("_*AutoExpert BA App v2.0*_")
        return sb.toString().trimEnd()
    }
}

@Composable
fun SummaryScreen(
    onBack: () -> Unit,
    onHome: () -> Unit = {},
    onCustomers: () -> Unit = {},
    onWallet: () -> Unit = {},
    onProfile: () -> Unit = {},
    vm: SummaryViewModel = hiltViewModel()
) {
    val state   = vm.state.collectAsState().value
    val context = LocalContext.current

    // Reload every time screen is visited
    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        bottomBar = {
            HomeBottomNavBar(selected = 3, unreadMessages = 0, onSelect = { i ->
                when (i) { 0 -> onHome(); 1 -> onCustomers(); 2 -> onWallet(); 4 -> onProfile() }
            })
        },
        containerColor = Color(0xFFF2F4F5)
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            Box(Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF003D2B), Color(0xFF005C40))))
                .padding(16.dp, 18.dp)) {
                Column {
                    Text("Daily Summary", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(Date()),
                        fontSize = 12.sp, color = Color.White.copy(0.6f))
                    Text("Shift: 10:00 AM - " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
                        fontSize = 11.sp, color = PetronasGreen)
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PetronasGreen)
                }
            } else {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SummaryKpiCard("Reach", state.todayReach.toString(), Modifier.weight(1f))
                        SummaryKpiCard("Litres", "%.1f".format(state.todayLitres) + "L", Modifier.weight(1f))
                        SummaryKpiCard("Commission", "Rs " + pkrFmt.format(state.todayCommission.toLong()), Modifier.weight(1f))
                    }

                    Row(Modifier.fillMaxWidth()
                        .background(Color(0xFF007273), RoundedCornerShape(10.dp))
                        .padding(14.dp, 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Unpaid Balance", fontSize = 12.sp, color = Color.White.copy(0.7f))
                        Text("Rs " + pkrFmt.format(state.unpaidBalance.toLong()),
                            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                    }

                    SummaryCard("Customer Breakdown") {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            BreakdownItem("Conquest", state.conquest, PetronasGreen)
                            BreakdownItem("Repeat", state.repeat, Color(0xFF3B82F6))
                            BreakdownItem("Existing", state.existing, Color(0xFFF59E0B))
                            BreakdownItem("Prospect", state.prospect, Color(0xFF9E9E9E))
                        }
                    }

                    if (state.vehicleCounts.isNotEmpty()) {
                        SummaryCard("By Vehicle") {
                            state.vehicleCounts.forEach { (name, count) ->
                                Row(Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(name, fontSize = 12.sp, color = TextPrimary)
                                    Text("$count customers", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                        }
                    }

                    if (state.productSales.isNotEmpty()) {
                        SummaryCard("Products Sold") {
                            state.productSales.forEach { (name, qty) ->
                                Row(Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(name, fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                                    val packs = state.packSales[name] ?: 0
                                    val qtyStr = if (packs > 0) {
                                        val ps = if (packs < 10) "0$packs" else "$packs"
                                        ps + " X " + "%.1f".format(qty) + "L"
                                    } else { "%.1f".format(qty) + "L" }
                                    Text(qtyStr, fontSize = 12.sp,
                                        color = PetronasGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        SummaryCard("Products Sold") {
                            Text("No products sold", fontSize = 12.sp, color = TextSecondary)
                        }
                    }

                    Button(
                        onClick = {
                            val report = vm.buildReportText(state)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, report)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Report"))
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PetronasGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Share, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Share Report", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryKpiCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.background(Color.White, RoundedCornerShape(10.dp)).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
private fun SummaryCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(10.dp)).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary,
            letterSpacing = 0.5.sp)
        content()
    }
}

@Composable
private fun BreakdownItem(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$count", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(label, fontSize = 9.sp, color = TextSecondary)
    }
}
