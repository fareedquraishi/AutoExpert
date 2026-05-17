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

            // Parse itemsJson for products
            val productMap = mutableMapOf<String, Double>()
            todayEntries.forEach { e ->
                try {
                    val json = e.itemsJson
                    if (json != "[]") {
                        val items = json.removeSurrounding("[", "]").split("},")
                        items.forEach { item ->
                            val name = Regex("""skuName":"([^"]+)""").find(item)?.groupValues?.get(1)
                            val qty  = Regex("""qty":([0-9.]+)""").find(item)?.groupValues?.get(1)?.toDoubleOrNull()
                            if (name != null && qty != null) {
                                productMap[name] = (productMap[name] ?: 0.0) + qty
                            }
                        }
                    }
                } catch (_: Exception) {}
            }

            // Vehicle counts
            val vehicleMap = mutableMapOf<String, Int>()
            todayEntries.forEach { e ->
                val v = e.vehicleTypeName ?: "Unknown"
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
        val cal  = Calendar.getInstance()
        val now  = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)

        val reachDiff = state.todayReach - state.yesterdayReach
        val litresDiff = state.todayLitres - state.yesterdayLitres
        val commDiff  = state.todayCommission - state.yesterdayCommission

        val reachArrow = if (reachDiff >= 0) "+$reachDiff" else "${reachDiff}"
        val litresArrow = if (litresDiff >= 0) "+" + "%.1f".format(litresDiff) + "L" else "-" + "%.1f".format(-litresDiff) + "L"
        val commArrow  = if (commDiff >= 0) "+" + "Rs " + pkrFmt.format(commDiff.toLong()) else "-" + "Rs " + pkrFmt.format((-commDiff).toLong())

        val products = state.productSales.entries.joinToString("\n") { "  - " + it.key + ": " + "%.1fL".format(it.value) }
            .ifEmpty { "  - No products sold" }
        val vehicles = state.vehicleCounts.entries.joinToString(" | ") { "${it.key}: ${it.value}" }
            .ifEmpty { "None" }

        return """
? Daily Summary ? $date
? Shift: 10:00 AM ? $now
? ${state.baName} | ${state.stationName}

? Reach: ${state.todayReach}/${state.reachTarget.toInt()}  $reachArrow
? Litres: ${"%.1f".format(state.todayLitres)}L/${state.litresTarget.toInt()}L  $litresArrow
? Today: Rs ${pkrFmt.format(state.todayCommission.toLong())}  $commArrow
? Unpaid Balance: Rs ${pkrFmt.format(state.unpaidBalance.toLong())}

? Breakdown:
? Conquest: ${state.conquest} | Repeat: ${state.repeat}
? Existing: ${state.existing} | Prospect: ${state.prospect}

? By Vehicle:
? $vehicles

? Products Sold:
$products

? Applicator: ${state.applicatorCount} customers

? AutoExpert BA App v2.0
        """.trimIndent()
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

    Scaffold(
        bottomBar = {
            HomeBottomNavBar(selected = 3, unreadMessages = 0, onSelect = { i ->
                when (i) { 0 -> onHome(); 1 -> onCustomers(); 2 -> onWallet(); 4 -> onProfile() }
            })
        },
        containerColor = Color(0xFFF2F4F5)
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            // Header
            Box(Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF003D2B), Color(0xFF005C40))))
                .padding(16.dp, 18.dp)) {
                Column {
                    Text("Daily Summary", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(Date()),
                        fontSize = 12.sp, color = Color.White.copy(0.6f))
                    Text("? Shift: 10:00 AM ? ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())}",
                        fontSize = 11.sp, color = PetronasGreen)
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PetronasGreen)
                }
            } else {
                val reachDiff = state.todayReach - state.yesterdayReach
                val litresDiff = state.todayLitres - state.yesterdayLitres
                val commDiff  = state.todayCommission - state.yesterdayCommission

                // KPI Cards
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    // KPI Row
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SummaryKpiCard("Reach", "${state.todayReach}/${state.reachTarget.toInt()}", reachDiff.toDouble(), Modifier.weight(1f))
                        SummaryKpiCard("Litres", "%.1fL".format(state.todayLitres), litresDiff, Modifier.weight(1f))
                        SummaryKpiCard("Commission", "Rs ${pkrFmt.format(state.todayCommission.toLong())}", commDiff, Modifier.weight(1f))
                    }

                    // Unpaid Balance
                    Row(Modifier.fillMaxWidth()
                        .background(Color(0xFF007273), RoundedCornerShape(10.dp))
                        .padding(14.dp, 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Unpaid Balance", fontSize = 12.sp, color = Color.White.copy(0.7f))
                        Text("Rs ${pkrFmt.format(state.unpaidBalance.toLong())}",
                            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                    }

                    // Breakdown
                    SummaryCard("Customer Breakdown") {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            BreakdownItem("Conquest", state.conquest, PetronasGreen)
                            BreakdownItem("Repeat", state.repeat, Color(0xFF3B82F6))
                            BreakdownItem("Existing", state.existing, Color(0xFFF59E0B))
                            BreakdownItem("Prospect", state.prospect, Color(0xFF9E9E9E))
                        }
                    }

                    // Vehicles
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

                    // Products
                    if (state.productSales.isNotEmpty()) {
                        SummaryCard("Products Sold") {
                            state.productSales.forEach { (name, qty) ->
                                Row(Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(name, fontSize = 12.sp, color = TextPrimary,
                                        modifier = Modifier.weight(1f))
                                    Text("%.1fL".format(qty), fontSize = 12.sp,
                                        color = PetronasGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Applicator
                    if (state.applicatorCount > 0) {
                        Row(Modifier.fillMaxWidth()
                            .background(Color(0xFF8B5CF6).copy(0.08f), RoundedCornerShape(10.dp))
                            .padding(14.dp, 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Applicator Customers", fontSize = 12.sp, color = TextPrimary)
                            Text("${state.applicatorCount}", fontSize = 14.sp,
                                fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
                        }
                    }

                    // Share Button
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
private fun SummaryKpiCard(label: String, value: String, diff: Double, modifier: Modifier = Modifier) {
    val isUp = diff >= 0
    Column(modifier.background(Color.White, RoundedCornerShape(10.dp)).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Icon(if (isUp) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                null, tint = if (isUp) PetronasGreen else Color(0xFFEF4444),
                modifier = Modifier.size(10.dp))
            Text("%.1f".format(kotlin.math.abs(diff)), fontSize = 9.sp,
                color = if (isUp) PetronasGreen else Color(0xFFEF4444))
        }
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
