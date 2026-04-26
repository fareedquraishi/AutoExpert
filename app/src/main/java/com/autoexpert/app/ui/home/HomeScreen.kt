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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.autoexpert.app.R
import com.autoexpert.app.data.local.entity.SaleEntryQueueEntity
import com.autoexpert.app.ui.components.*
import com.autoexpert.app.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

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
    val ctx = androidx.compose.ui.platform.LocalContext.current
    androidx.compose.runtime.LaunchedEffect(ui.syncError) {
        if (ui.syncError.isNotEmpty()) {
            android.util.Log.e("AutoExpert", "SyncError: " + ui.syncError)
            android.widget.Toast.makeText(ctx, ui.syncError, android.widget.Toast.LENGTH_LONG).show()
        }
    }
    var selectedNav by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selected = selectedNav,
                unreadMessages = ui.unreadMessages,
                onSelect = { idx ->
                    selectedNav = idx
                    when (idx) {
                        0 -> {}
                        1 -> onOpenCustomers()
                        2 -> onOpenChat()
                        3 -> onOpenWallet()
                        4 -> onOpenProfile()
                    }
                }
            )
        },
        containerColor = BackgroundGray
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())) {

            // ── Dark header ──────────────────────────────────────────────
            Box(
                Modifier.fillMaxWidth()
                    .background(HomeHeaderGradient)
            ) {
                // Glow
                Box(
                    Modifier.size(280.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 80.dp, y = (-40).dp)
                        .background(
                            Brush.radialGradient(listOf(PetronasGreen.copy(.16f), Color.Transparent)),
                            CircleShape
                        )
                )

                Column(Modifier.statusBarsPadding().padding(15.dp, 16.dp, 15.dp, 0.dp)) {

                    // Top row: BA info + icons
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Good morning 👋",
                                fontSize = 10.sp, color = Color.White.copy(.42f), fontWeight = FontWeight.Medium
                            )
                            Text(
                                ui.baName.ifEmpty { "Loading…" },
                                fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White
                            )
                            Text(
                                "📍 ${ui.stationName.ifEmpty { "—" }}",
                                fontSize = 10.sp, color = PetronasGreen.copy(.85f), fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(7.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            // Notification bell
                            IconBadgeButton(icon = "🔔", badge = ui.unreadNotices > 0, onClick = onOpenNotices)
                            // Chat
                            IconBadgeButton(icon = "💬", badge = ui.unreadMessages > 0, onClick = onOpenChat)
                            // Euro logo
                            Box(
                                Modifier.size(34.dp)
                                    .background(Color.White.copy(.06f), RoundedCornerShape(10.dp))
                                    .border(1.dp, Color.White.copy(.09f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(painterResource(R.drawable.euro_logo), "Euro", Modifier.size(22.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // KPI grid 2x2
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KpiCard(
                            label = "TODAY REACH", modifier = Modifier.weight(1f),
                            value = ui.todayReach.toString(),
                            sub = "Target ${ui.reachTarget.toInt()} · ${maxOf(0, ui.reachTarget.toInt() - ui.todayReach)} left",
                            progress = if (ui.reachTarget > 0) (ui.todayReach / ui.reachTarget).toFloat() else 0f
                        )
                        KpiCard(
                            label = "LITRES SOLD", modifier = Modifier.weight(1f),
                            value = "${ui.todayLitres}L",
                            sub = "Target ${ui.litresTarget.toInt()}L · ${maxOf(0.0, ui.litresTarget - ui.todayLitres)}L left",
                            progress = if (ui.litresTarget > 0) (ui.todayLitres / ui.litresTarget).toFloat() else 0f
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KpiCard(
                            label = "COMMISSION", modifier = Modifier.weight(1f),
                            value = "₨ ${pkrFmt.format(ui.todayCommission.toLong())}",
                            sub = "Unpaid ₨ ${pkrFmt.format(ui.unpaidBalance.toLong())}"
                        )
                        KpiCard(
                            label = "ATTENDANCE", modifier = Modifier.weight(1f),
                            value = "${ui.attendanceThisMonth}/30",
                            sub = if (ui.todayAttendanceMarked) "✅ GPS Marked" else "⏳ Not marked"
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                }
            }

            // ── Scrollable body ──────────────────────────────────────────
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(13.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Notice banner
                if (ui.unreadNotices > 0) {
                    item {
                        NoticeBanner(
                            onClick = onOpenNotices,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }

                // Action buttons
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNewCustomer,
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                Modifier.fillMaxSize()
                                    .background(GreenGradient, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("＋ New Customer", color = Color.White,
                                    fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                        OutlinedButton(
                            onClick = { /* submit without product */ },
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, BorderColor),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                        ) {
                            Text("📋 No Product", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // Today's customers header
                item {
                    SectionHeader(
                        title = "TODAY'S CUSTOMERS",
                        icon = "👥",
                        action = "View all →",
                        onAction = onOpenCustomers
                    )
                }

                if (ui.todayCustomers.isEmpty()) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("🏃", fontSize = 36.sp)
                                Text("No customers yet today", fontSize = 13.sp,
                                    color = TextSecondary, fontWeight = FontWeight.Medium)
                                Text("Tap '+ New Customer' to add your first",
                                    fontSize = 11.sp, color = TextDim)
                            }
                        }
                    }
                }

                items(ui.todayCustomers) { entry ->
                    CustomerCard(entry = entry, modifier = Modifier.padding(bottom = 7.dp))
                }
            }
        }
    }
}

@Composable
private fun IconBadgeButton(icon: String, badge: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(Color.White.copy(.08f), RoundedCornerShape(10.dp))
            .border(1.dp, Color.White.copy(.1f), RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(icon, fontSize = 15.sp)
        if (badge) {
            Box(
                Modifier.size(8.dp).align(Alignment.TopEnd).offset(x = (-5).dp, y = 5.dp)
                    .background(AccentRed, CircleShape)
                    .border(1.5.dp, Color(0xFF0A1628), CircleShape)
            )
        }
    }
}

@Composable
private fun NoticeBanner(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(listOf(Color(0xFF1A3050), Color(0xFF0D2418)))
            )
            .border(1.dp, PetronasGreen.copy(.18f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(12.dp, 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("📢", fontSize = 18.sp)
        Column(Modifier.weight(1f)) {
            Text("New Notice from Admin", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Tap to read", fontSize = 10.sp, color = Color.White.copy(.45f), modifier = Modifier.padding(top = 1.dp))
        }
        Text("›", fontSize = 18.sp, color = PetronasGreen, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CustomerCard(entry: SaleEntryQueueEntity, modifier: Modifier = Modifier) {
    val initial = entry.customerName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val (avatarBrush, badgeType, badgeText) = when {
        !entry.isRepeat && entry.totalLitres > 0 ->
            Triple(Brush.linearGradient(listOf(PetronasGreen, PetronasGreenDark)), BadgeType.GREEN, "Conquest")
        entry.isRepeat && entry.totalLitres > 0 ->
            Triple(Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))), BadgeType.BLUE, "Repeat")
        entry.isRepeat ->
            Triple(Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))), BadgeType.AMBER, "Existing")
        entry.isApplicator ->
            Triple(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))), BadgeType.PURPLE, "✦ App")
        else ->
            Triple(Brush.linearGradient(listOf(Color(0xFF6B7280), Color(0xFF4B5563))), BadgeType.GRAY, "Prospect")
    }

    Row(
        modifier = modifier.fillMaxWidth()
            .background(Color.White, RoundedCornerShape(13.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(13.dp))
            .padding(10.dp, 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Avatar
        Box(
            Modifier.size(36.dp).background(avatarBrush, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(initial, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
        // Info
        Column(Modifier.weight(1f)) {
            Text(entry.customerName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(
                buildString {
                    append(entry.vehicleTypeName?.let { "• $it " } ?: "")
                    if (entry.totalLitres > 0) append("• ${entry.totalLitres}L")
                    else append("• No sale")
                    if (entry.syncStatus == "pending") append(" ⏳")
                },
                fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(top = 1.dp)
            )
        }
        StatusBadge(badgeText, badgeType)
    }
}

@Composable
private fun BottomNavBar(selected: Int, unreadMessages: Int, onSelect: (Int) -> Unit) {
    val items = listOf("Home" to "🏠", "Customers" to "👥", "Chat" to "💬", "Wallet" to "💰", "Profile" to "👤")
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEachIndexed { idx, (label, icon) ->
                val isActive = selected == idx
                Box(
                    modifier = Modifier.weight(1f)
                        .clickable { onSelect(idx) }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Active indicator
                    if (isActive) {
                        Box(
                            Modifier.align(Alignment.TopCenter).fillMaxWidth(.5f).height(2.dp)
                                .background(PetronasGreen, RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box {
                            Text(icon, fontSize = 18.sp)
                            if (idx == 2 && unreadMessages > 0) {
                                Box(
                                    Modifier.size(14.dp).align(Alignment.TopEnd).offset(x = 4.dp, y = (-2).dp)
                                        .background(AccentRed, CircleShape)
                                        .border(1.5.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (unreadMessages > 9) "9+" else unreadMessages.toString(),
                                        fontSize = 7.sp, color = Color.White, fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            label, fontSize = 9.sp, fontWeight = FontWeight.Bold,
                            color = if (isActive) PetronasGreen else TextDim,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
