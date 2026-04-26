package com.autoexpert.app.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.autoexpert.app.data.local.dao.AttendanceQueueDao
import com.autoexpert.app.data.local.dao.LeaveRequestDao
import com.autoexpert.app.data.local.entity.LeaveRequestEntity
import com.autoexpert.app.ui.components.*
import com.autoexpert.app.ui.theme.*
import com.autoexpert.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val session: SessionManager,
    private val leaveDao: LeaveRequestDao,
    private val attendanceDao: AttendanceQueueDao,
) : ViewModel() {
    val baName      = session.baName.filterNotNull().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val stationName = session.stationName.filterNotNull().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val leaveRequests: StateFlow<List<LeaveRequestEntity>> = session.baId
        .filterNotNull()
        .flatMapLatest { leaveDao.getByBa(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val presentDays = MutableStateFlow(0)
    val monthDays   = MutableStateFlow(Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH))

    init {
        viewModelScope.launch {
            val monthPrefix = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
            session.baId.filterNotNull().collect { baId ->
                presentDays.value = attendanceDao.countPresentDays(baId, monthPrefix)
            }
        }
    }

    fun logout() = viewModelScope.launch { session.clearSession() }
}

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    vm: ProfileViewModel = hiltViewModel()
) {
    val baName      by vm.baName.collectAsState()
    val stationName by vm.stationName.collectAsState()
    val leaves      by vm.leaveRequests.collectAsState()
    val present     by vm.presentDays.collectAsState()
    val total       by vm.monthDays.collectAsState()
    var showLogout  by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(BackgroundGray)) {
        // Header bar with avatar
        Box(
            Modifier.fillMaxWidth()
                .background(HomeHeaderGradient)
                .statusBarsPadding()
                .padding(16.dp, 20.dp, 16.dp, 28.dp)
        ) {
            Row(Modifier.align(Alignment.TopStart)) {
                IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                    Box(Modifier.fillMaxSize().background(Color.White.copy(.09f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center) {
                        Text("←", fontSize = 16.sp, color = Color.White)
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier.size(72.dp)
                        .background(PetronasGreen.copy(.18f), CircleShape)
                        .border(2.dp, PetronasGreen.copy(.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        baName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(baName, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text(stationName, fontSize = 11.sp, color = PetronasGreen.copy(.85f),
                    fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 3.dp))
            }
        }

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Attendance card
            Surface(color = Color.White, shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderColor), shadowElevation = 1.dp) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    SectionHeader("ATTENDANCE THIS MONTH", "📅")
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        AttStat("Present", "$present", PetronasGreen)
                        Box(Modifier.width(1.dp).height(40.dp).background(BorderColor))
                        AttStat("Absent", "${maxOf(0, total - present)}", AccentRed)
                        Box(Modifier.width(1.dp).height(40.dp).background(BorderColor))
                        AttStat("Rate", "${if (total > 0) (present * 100 / total) else 0}%", AccentBlue)
                    }
                    Spacer(Modifier.height(10.dp))
                    // Month progress bar
                    Box(Modifier.fillMaxWidth().height(4.dp).background(BorderColor, RoundedCornerShape(2.dp))) {
                        Box(Modifier.fillMaxWidth(if (total > 0) present.toFloat() / total else 0f)
                            .fillMaxHeight().background(GreenGradient, RoundedCornerShape(2.dp)))
                    }
                }
            }

            // Leave requests
            Surface(color = Color.White, shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderColor), shadowElevation = 1.dp) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    SectionHeader("LEAVE REQUESTS", "🏖️")
                    if (leaves.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                            Text("No leave requests yet", fontSize = 12.sp, color = TextSecondary)
                        }
                    } else {
                        leaves.take(6).forEachIndexed { i, leave ->
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(leave.leaveType, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("${leave.fromDate}  →  ${leave.toDate}",
                                        fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(top = 1.dp))
                                }
                                StatusBadge(
                                    text = leave.status.replaceFirstChar { it.uppercaseChar() },
                                    type = when (leave.status) {
                                        "approved" -> BadgeType.GREEN
                                        "rejected" -> BadgeType.RED
                                        else       -> BadgeType.AMBER
                                    }
                                )
                            }
                            if (i < leaves.size - 1) HorizontalDivider(color = BorderColor.copy(.5f))
                        }
                    }
                }
            }

            // App info
            Surface(color = Color.White, shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderColor), shadowElevation = 1.dp) {
                Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeader("APP INFO", "ℹ️")
                    InfoRow("Version", "4.0.0")
                    InfoRow("Powered by", "Fintectual Pvt Ltd")
                    InfoRow("Platform", "Petronas Auto Expert Centre")
                }
            }

            // Logout button
            OutlinedButton(
                onClick = { showLogout = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, AccentRed.copy(.4f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed)
            ) {
                Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(Modifier.height(20.dp))
        }
    }

    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            title = { Text("Sign Out?", fontWeight = FontWeight.Bold) },
            text  = { Text("Are you sure you want to sign out of your account?") },
            confirmButton = {
                TextButton(onClick = { vm.logout(); onLogout() }) {
                    Text("Sign Out", color = AccentRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogout = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AttStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(label, fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}
