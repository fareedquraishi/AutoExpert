content = '''\
package com.autoexpert.app.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
            HomeBottomNavBar(
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
        containerColor = Color(0xFFF2F4F5)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            // ── Header ──
            item {
                Box(
                    Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Color(0xFF003D2B), Color(0xFF005C40))))
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                ) {
                    Column {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(greetingText(), fontSize = 11.sp, color = Color.White.copy(0.6f))
                                Text(
                                    ui.baName.ifEmpty { "Loading..." },
                                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.LocationOn, null,
                                        tint = PetronasGreen, modifier = Modifier.size(12.dp))
                                    Spacer(Modifier.width(3.dp))
                                    Text(ui.stationName.ifEmpty { "—" }, fontSize = 12.sp, color = PetronasGreen)
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                HeaderIconBtn(
                                    icon = Icons.Filled.Notifications,
                                    badge = ui.unreadNotices,
                                    onClick = onOpenNotices
                                )
                                HeaderIconBtn(icon = Icons.Filled.Chat, onClick = onOpenChat)
                                HeaderIconBtn(icon = Icons.Filled.Refresh, onClick = { vm.syncFromSupabase() })
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // ── KPI Strip ──
                        Column(
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(0.1f))
                        ) {
                            KpiRow(
                                label = "REACH",
                                col1Val = "${ui.todayReach}", col1Lbl = "Today",
                                col2Val = "%.0f".format(ui.reachTarget), col2Lbl = "Target",
                                col3Val = "%.0f".format(maxOf(0.0, ui.reachTarget - ui.todayReach)), col3Lbl = "Left",
                                col4Val = "Rs ${pkrFmt.format(ui.todayCommission.toLong())}", col4Lbl = "Earned",
                            )
                            Divider(color = Color.White.copy(0.1f), thickness = 0.5.dp)
                            KpiRow(
                                label = "LITRES",
                                col1Val = "%.1fL".format(ui.todayLitres), col1Lbl = "Sold",
                                col2Val = "%.0fL".format(ui.litresTarget), col2Lbl = "Target",
                                col3Val = "%.1fL".format(maxOf(0.0, ui.litresTarget - ui.todayLitres)), col3Lbl = "Left",
                                col4Val = "${ui.todayConversions}", col4Lbl = "Conv.",
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
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1B4332))
                            .clickable { onOpenNotices() }
                            .padding(12.dp, 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Filled.Campaign, null, tint = PetronasGreen, modifier = Modifier.size(20.dp))
                        Column(Modifier.weight(1f)) {
                            Text("New Notice from Admin", fontSize = 12.sp,
                                fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Tap to read", fontSize = 10.sp, color = Color.White.copy(0.5f))
                        }
                        Icon(Icons.Filled.ChevronRight, null, tint = PetronasGreen)
                    }
                }
            }

            // ── New Customer Button ──
            item {
                Button(
                    onClick = onNewCustomer,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp).height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PetronasGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.PersonAdd, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("+ New Customer", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            // ── Customers Header ──
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Icon(Icons.Filled.Group, null,
                            tint = TextSecondary, modifier = Modifier.size(14.dp))
                        Text("TODAY\'S CUSTOMERS", fontSize = 10.sp,
                            fontWeight = FontWeight.Bold, color = TextSecondary)
                    }
                    Text("View all", fontSize = 11.sp, color = PetronasGreen,
                        modifier = Modifier.clickable { onOpenCustomers() })
                }
            }

            // ── Customer Cards ──
            if (ui.todayCustomers.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Filled.DirectionsRun, null,
                            tint = TextSecondary, modifier = Modifier.size(40.dp))
                        Text("No customers yet today", fontSize = 13.sp, color = TextSecondary)
                        Text("Tap '+ New Customer' to add your first",
                            fontSize = 11.sp, color = TextSecondary)
                    }
                }
            } else {
                items(ui.todayCustomers) { entry ->
                    CustomerCard(
                        entry = entry,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderIconBtn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badge: Int = 0,
    onClick: () -> Unit
) {
    Box(
        Modifier.size(36.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(Color.White.copy(0.12f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        BadgedBox(badge = {
            if (badge > 0) Badge(containerColor = Color.Red) {
                Text("$badge", fontSize = 8.sp, color = Color.White)
            }
        }) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
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
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold,
            color = Color.White.copy(0.45f), modifier = Modifier.width(38.dp))
        KpiCell(col1Val, col1Lbl, Modifier.weight(1f))
        KpiCell(col2Val, col2Lbl, Modifier.weight(1f))
        KpiCell(col3Val, col3Lbl, Modifier.weight(1f), amber = true)
        KpiCell(col4Val, col4Lbl, Modifier.weight(1.3f), green = true)
    }
}

@Composable
private fun KpiCell(
    value: String, label: String,
    modifier: Modifier = Modifier,
    amber: Boolean = false, green: Boolean = false
) {
    Column(modifier.padding(horizontal = 1.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            color = when { green -> PetronasGreen; amber -> Color(0xFFFBBF24); else -> Color.White },
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(label, fontSize = 8.sp, color = Color.White.copy(0.45f))
    }
}

@Composable
private fun CustomerCard(entry: SaleEntryQueueEntity, modifier: Modifier = Modifier) {
    val isSale   = entry.totalLitres > 0
    val isRepeat = entry.isRepeat

    val (dotColor, badgeText, badgeColor) = when {
        !isRepeat && isSale  -> Triple(PetronasGreen,    "Conquest", PetronasGreen)
        isRepeat  && isSale  -> Triple(Color(0xFF1565C0), "Repeat",   Color(0xFF1565C0))
        isRepeat  && !isSale -> Triple(Color(0xFFF59E0B), "Existing", Color(0xFFF59E0B))
        else                 -> Triple(Color(0xFF9E9E9E), "Prospect", Color(0xFF9E9E9E))
    }

    val timeStr = try {
        val d = SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss", Locale.getDefault())
            .parse(entry.entryTime.take(19))
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(d ?: Date())
    } catch (_: Exception) { "" }

    val vehicleLabel = when (entry.vehicleTypeName?.lowercase()?.trim()) {
        "car", "sedan", "suv"     -> "Car"
        "motorcycle", "bike"      -> "Bike"
        "rickshaw", "auto"        -> "Auto"
        "truck", "van"            -> "Truck"
        "tractor"                 -> "Tractor"
        else                      -> entry.vehicleTypeName ?: ""
    }

    Row(
        modifier = modifier.fillMaxWidth()
            .background(Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFFE8ECF0), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar
        Box(
            Modifier.size(32.dp)
                .background(dotColor.copy(0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                entry.customerName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                fontSize = 13.sp, fontWeight = FontWeight.Bold, color = dotColor
            )
        }

        // Info
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            // Name + badge
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(entry.customerName, fontSize = 12.sp,
                    fontWeight = FontWeight.Bold, color = TextPrimary)
                Box(
                    Modifier.background(badgeColor.copy(0.1f), RoundedCornerShape(3.dp))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(badgeText, fontSize = 8.sp,
                        fontWeight = FontWeight.Bold, color = badgeColor)
                }
            }

            // Vehicle + time on one line
            Text(
                buildString {
                    if (vehicleLabel.isNotEmpty()) append(vehicleLabel)
                    if (vehicleLabel.isNotEmpty() && timeStr.isNotEmpty()) append(" · ")
                    append(timeStr)
                },
                fontSize = 10.sp, color = Color(0xFFAAAAAA)
            )

            // Product info
            if (isSale) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("%.1fL".format(entry.totalLitres),
                        fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                        color = PetronasGreen)
                    if (entry.isApplicator) {
                        Box(Modifier.background(Color(0xFF8B5CF6).copy(0.1f),
                            RoundedCornerShape(3.dp)).padding(horizontal = 4.dp, vertical = 1.dp)) {
                            Text("App", fontSize = 8.sp, color = Color(0xFF8B5CF6),
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Text(if (isRepeat) "Visited — no purchase" else "No purchase",
                    fontSize = 10.sp, color = Color(0xFFAAAAAA))
            }
        }

        // Right: commission
        if (isSale) {
            Column(horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Rs ${pkrFmt.format(entry.totalCommission.toLong())}",
                    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PetronasGreen)
            }
        }
    }
}

@Composable
fun HomeBottomNavBar(selected: Int, unreadMessages: Int, onSelect: (Int) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 6.dp) {
        data class NavItem(val label: String,
                           val icon: androidx.compose.ui.graphics.vector.ImageVector, val idx: Int)
        val items = listOf(
            NavItem("Home",      Icons.Filled.Home,         0),
            NavItem("Customers", Icons.Filled.People,       1),
            NavItem("Chat",      Icons.Filled.Chat,         2),
            NavItem("Wallet",    Icons.Filled.AccountBalanceWallet, 3),
            NavItem("Profile",   Icons.Filled.Person,       4),
        )
        items.forEach { item ->
            NavigationBarItem(
                selected = selected == item.idx,
                onClick  = { onSelect(item.idx) },
                icon = {
                    BadgedBox(badge = {
                        if (item.idx == 2 && unreadMessages > 0)
                            Badge { Text("$unreadMessages") }
                    }) {
                        Icon(item.icon, item.label, modifier = Modifier.size(22.dp))
                    }
                },
                label = { Text(item.label, fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = PetronasGreen,
                    selectedTextColor   = PetronasGreen,
                    unselectedIconColor = Color(0xFF9E9E9E),
                    indicatorColor      = PetronasGreen.copy(0.1f)
                )
            )
        }
    }
}

private fun greetingText(): String {
    val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when { h < 12 -> "Good morning"; h < 17 -> "Good afternoon"; else -> "Good evening" }
}
'''

dst = "app/src/main/java/com/autoexpert/app/ui/home/HomeScreen.kt"
with open(dst, "w", encoding="ascii", errors="replace") as f:
    f.write(content)
print(f"Written {content.count(chr(10))} lines to {dst}")
