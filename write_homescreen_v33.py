content = '''\
package com.autoexpert.app.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.autoexpert.app.data.local.entity.SaleEntryQueueEntity
import com.autoexpert.app.ui.components.*
import com.autoexpert.app.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private val pkrFmt = NumberFormat.getNumberInstance(Locale.US)

@Composable
fun HomeScreen(
    onNewCustomer: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenNotices: () -> Unit,
    onOpenCustomers: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenProfile: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val ui by vm.uiState.collectAsState()
    var selectedNav by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selected = selectedNav,
                unreadMessages = ui.unreadMessages,
                onSelect = { i ->
                    selectedNav = i
                    when (i) {
                        1 -> onOpenCustomers()
                        2 -> onOpenChat()
                        3 -> onOpenWallet()
                        4 -> onOpenProfile()
                    }
                }
            )
        },
        containerColor = SurfaceGray
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // ── Header ──
            item {
                Box(
                    Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(PetronasDark, Color(0xFF1a3a2a))))
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Column {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    greetingText(),
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    ui.baName.ifEmpty { "Loading..." },
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("📍", fontSize = 11.sp)
                                    Text(
                                        ui.stationName.ifEmpty { "—" },
                                        fontSize = 12.sp,
                                        color = PetronasGreen
                                    )
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = onOpenNotices,
                                    modifier = Modifier.size(38.dp)
                                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                ) {
                                    BadgedBox(badge = {
                                        if (ui.unreadNotices > 0) Badge { Text("${ui.unreadNotices}") }
                                    }) {
                                        Text("🔔", fontSize = 16.sp)
                                    }
                                }
                                IconButton(
                                    onClick = onOpenChat,
                                    modifier = Modifier.size(38.dp)
                                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                ) {
                                    Text("💬", fontSize = 16.sp)
                                }
                                IconButton(
                                    onClick = { vm.syncFromSupabase() },
                                    modifier = Modifier.size(38.dp)
                                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                ) {
                                    Text("↻", fontSize = 18.sp, color = Color.White)
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // ── KPI Strip Table ──
                        Column(
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                        ) {
                            // Row 1: REACH
                            KpiRow(
                                label = "REACH",
                                col1Val = "${ui.todayReach}", col1Lbl = "Today",
                                col2Val = "%.0f".format(ui.reachTarget), col2Lbl = "Target",
                                col3Val = "%.0f".format(maxOf(0.0, ui.reachTarget - ui.todayReach)), col3Lbl = "Left",
                                col4Val = "PKR ${pkrFmt.format(ui.todayCommission.toLong())}", col4Lbl = "Earned",
                                col4Green = true
                            )
                            Divider(color = Color.White.copy(alpha = 0.1f), thickness = 0.5.dp)
                            // Row 2: LITRES
                            KpiRow(
                                label = "LITRES",
                                col1Val = "%.1fL".format(ui.todayLitres), col1Lbl = "Sold",
                                col2Val = "%.0fL".format(ui.litresTarget), col2Lbl = "Target",
                                col3Val = "%.1fL".format(maxOf(0.0, ui.litresTarget - ui.todayLitres)), col3Lbl = "Left",
                                col4Val = "${ui.todayConversions}", col4Lbl = "Conv.",
                                col4Green = true
                            )
                        }
                    }
                }
            }

            // ── Notice Banner ──
            if (ui.unreadNotices > 0) {
                item {
                    Row(
                        Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1a3a2a))
                            .clickable { onOpenNotices() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("📢", fontSize = 18.sp)
                        Column(Modifier.weight(1f)) {
                            Text("New Notice from Admin", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Tap to read", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                        Text("›", fontSize = 20.sp, color = PetronasGreen)
                    }
                }
            }

            // ── Action Buttons ──
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onNewCustomer,
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PetronasGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+ New Customer", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // ── Today's Customers Header ──
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👥", fontSize = 14.sp)
                        Text("TODAY\'S CUSTOMERS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    }
                    Text(
                        "View all →",
                        fontSize = 12.sp,
                        color = PetronasGreen,
                        modifier = Modifier.clickable { onOpenCustomers() }
                    )
                }
            }

            // ── Customer Cards ──
            if (ui.todayCustomers.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🏃", fontSize = 40.sp)
                        Text("No customers yet today", fontSize = 14.sp, color = TextSecondary)
                        Text("Tap \\'+ New Customer\\' to add your first", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            } else {
                items(ui.todayCustomers) { entry ->
                    CustomerCard(
                        entry = entry,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun KpiRow(
    label: String,
    col1Val: String, col1Lbl: String,
    col2Val: String, col2Lbl: String,
    col3Val: String, col3Lbl: String,
    col4Val: String, col4Lbl: String,
    col4Green: Boolean = false
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.width(42.dp)
        )
        KpiCell(col1Val, col1Lbl, Modifier.weight(1f))
        KpiCell(col2Val, col2Lbl, Modifier.weight(1f))
        KpiCell(col3Val, col3Lbl, Modifier.weight(1f), highlight = true)
        KpiCell(col4Val, col4Lbl, Modifier.weight(1.4f), green = col4Green)
    }
}

@Composable
private fun KpiCell(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
    green: Boolean = false
) {
    Column(modifier.padding(horizontal = 2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = when {
                green -> PetronasGreen
                highlight -> Color(0xFFFBBF24)
                else -> Color.White
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(label, fontSize = 9.sp, color = Color.White.copy(alpha = 0.5f))
    }
}

@Composable
private fun CustomerCard(entry: SaleEntryQueueEntity, modifier: Modifier = Modifier) {
    val isSale = entry.totalLitres > 0
    val isRepeat = entry.isRepeat

    val (bgColor, dotColor, badgeText, badgeColor) = when {
        !isRepeat && isSale  -> listOf(Color(0xFFE8F5E9), PetronasGreen,    "Conquest", PetronasGreen)
        isRepeat  && isSale  -> listOf(Color(0xFFE3F2FD), Color(0xFF1565C0), "Repeat",   Color(0xFF1565C0))
        isRepeat  && !isSale -> listOf(Color(0xFFFFF8E1), Color(0xFFF59E0B), "Existing", Color(0xFFF59E0B))
        else                 -> listOf(Color(0xFFF5F5F5), Color(0xFF9E9E9E), "Prospect", Color(0xFF9E9E9E))
    }

    val timeStr = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss", Locale.getDefault())
        val d = sdf.parse(entry.entryTime.take(19))
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(d ?: Date())
    } catch (_: Exception) { "" }

    Row(
        modifier = modifier.fillMaxWidth()
            .background(Color.White, RoundedCornerShape(13.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(13.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Avatar dot
        Box(
            Modifier.size(36.dp)
                .background(bgColor as Color, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (isSale) "●" else "○",
                fontSize = 14.sp,
                color = dotColor as Color
            )
        }

        // Info
        Column(Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    entry.customerName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Box(
                    Modifier.background(
                        (badgeColor as Color).copy(alpha = 0.12f),
                        RoundedCornerShape(4.dp)
                    ).padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        badgeText as String,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            // Vehicle + time
            val vehicleIcon = when (entry.vehicleTypeName?.lowercase()) {
                "car", "sedan", "suv" -> "🚗"
                "motorcycle", "bike"  -> "🏍"
                "rickshaw", "auto"    -> "🛺"
                "truck", "van"        -> "🚛"
                "tractor"             -> "🚜"
                else                  -> "🚗"
            }
            Text(
                "$vehicleIcon $timeStr",
                fontSize = 10.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Product lines
            if (isSale) {
                Text(
                    "%.1fL".format(entry.totalLitres) +
                        if (entry.isApplicator) " · ✦App" else "",
                    fontSize = 10.sp,
                    color = PetronasGreen,
                    modifier = Modifier.padding(top = 1.dp)
                )
            } else {
                Text(
                    if (isRepeat) "Visited — no purchase" else "First contact — no purchase",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }

        // Right: commission + litres
        Column(horizontalAlignment = Alignment.End) {
            if (isSale) {
                Text(
                    "PKR ${pkrFmt.format(entry.totalCommission.toLong())}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PetronasGreen
                )
                Text(
                    "%.1fL".format(entry.totalLitres),
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            } else {
                Text("—", fontSize = 14.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun BottomNavBar(selected: Int, unreadMessages: Int, onSelect: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("Home", "🏠", 0),
            Triple("Customers", "👥", 1),
            Triple("Chat", "💬", 2),
            Triple("Wallet", "💰", 3),
            Triple("Profile", "👤", 4),
        )
        items.forEach { (label, icon, idx) ->
            NavigationBarItem(
                selected = selected == idx,
                onClick = { onSelect(idx) },
                icon = {
                    BadgedBox(badge = {
                        if (idx == 2 && unreadMessages > 0) Badge { Text("$unreadMessages") }
                    }) { Text(icon, fontSize = 18.sp) }
                },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PetronasGreen,
                    selectedTextColor = PetronasGreen,
                    indicatorColor = PetronasGreen.copy(alpha = 0.1f)
                )
            )
        }
    }
}

private fun greetingText(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning 👋"
        hour < 17 -> "Good afternoon 👋"
        else      -> "Good evening 👋"
    }
}
'''

dst = "app/src/main/java/com/autoexpert/app/ui/home/HomeScreen.kt"
with open(dst, "w", encoding="ascii", errors="replace") as f:
    f.write(content)
print(f"Written {content.count(chr(10))} lines to {dst}")
