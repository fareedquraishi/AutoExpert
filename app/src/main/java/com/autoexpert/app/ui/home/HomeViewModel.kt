package com.autoexpert.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoexpert.app.BuildConfig
import com.autoexpert.app.data.local.dao.*
import com.autoexpert.app.data.local.entity.*
import com.autoexpert.app.data.remote.api.SupabaseApi
import com.autoexpert.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class HomeUiState(
    val baName: String = "",
    val stationName: String = "",
    val todayReach: Int = 0,
    val todayLitres: Double = 0.0,
    val todayCommission: Double = 0.0,
    val reachTarget: Double = 100.0,
    val litresTarget: Double = 100.0,
    val unreadNotices: Int = 0,
    val unreadMessages: Int = 0,
    val attendanceThisMonth: Int = 0,
    val unpaidBalance: Double = 0.0,
    val todayCustomers: List<SaleEntryQueueEntity> = emptyList(),
    val todayAttendanceMarked: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val session: SessionManager,
    private val saleDao: SaleEntryQueueDao,
    private val noticeDao: NoticeDao,
    private val messageDao: MessageDao,
    private val payoutDao: PayoutDao,
    private val attendanceDao: AttendanceQueueDao,
    private val targetDao: TargetDao,
    private val skuDao: SkuDao,
    private val vehicleTypeDao: VehicleTypeDao,
    private val competitorBrandDao: CompetitorBrandDao,
    private val api: SupabaseApi,
) : ViewModel() {

    private val today       = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private val monthPrefix = today.substring(0, 7)
    private val apiKey      = BuildConfig.SUPABASE_ANON_KEY
    private val authHeader  = "Bearer $apiKey"

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadData()
        syncRemoteData()
    }

    private fun loadData() {
        // BA name + station name from session
        viewModelScope.launch {
            combine(
                session.baName.map { it ?: "" },
                session.stationName.map { it ?: "" },
            ) { name, station -> name to station }
                .collect { (name, station) ->
                    _uiState.update { it.copy(baName = name, stationName = station) }
                }
        }

        // Today's sale entries from local Room
        viewModelScope.launch {
            session.baId.map { it ?: "" }.filter { it.isNotEmpty() }.collect { baId ->
                saleDao.getByBaAndDate(baId, today).collect { entries ->
                    _uiState.update { s -> s.copy(
                        todayReach      = entries.size,
                        todayLitres     = entries.sumOf { it.totalLitres },
                        todayCommission = entries.sumOf { it.totalCommission },
                        todayCustomers  = entries
                    )}
                }
            }
        }

        // Unread counts
        viewModelScope.launch {
            noticeDao.getUnreadCount().collect { count ->
                _uiState.update { it.copy(unreadNotices = count) }
            }
        }
        viewModelScope.launch {
            messageDao.getUnreadCount().collect { count ->
                _uiState.update { it.copy(unreadMessages = count) }
            }
        }

        // Attendance + targets + unpaid balance
        viewModelScope.launch {
            session.baId.map { it ?: "" }.filter { it.isNotEmpty() }.collect { baId ->
                val unpaid      = 0.0 // payouts fetched via sync
                val presentDays = attendanceDao.countPresentDays(baId, monthPrefix)
                val att         = attendanceDao.getByBaAndDate(baId, today)
                val stationId   = session.stationId.first() ?: ""
                val target      = targetDao.getActiveTarget(baId, stationId, today)

                _uiState.update { it.copy(
                    unpaidBalance         = unpaid,
                    attendanceThisMonth   = presentDays,
                    todayAttendanceMarked = att != null,
                    reachTarget  = if (target?.targetBasis == "reach"  || target?.targetBasis == "entries") target.targetValue else 100.0,
                    litresTarget = if (target?.targetBasis == "litres" || target?.targetBasis == "volume")  target.targetValue else 100.0,
                )}
            }
        }
    }

    fun syncRemoteData() {
        viewModelScope.launch {
            val baId = session.baId.first() ?: return@launch
            try {
                // Reference data ? SKUs, vehicle types, competitor brands
                api.getSkus(apiKey = apiKey, auth = authHeader).body()?.let { list ->
                    skuDao.upsertAll(list.map { s -> SkuEntity(
                        id = s.id, name = s.name, productType = s.productType,
                        volumeMl = s.volumeMl, purchasePrice = s.purchasePrice,
                        marginPercent = s.marginPercent, sellingPrice = s.sellingPrice,
                        isActive = s.isActive
                    )})
                }
                api.getVehicleTypes(apiKey = apiKey, auth = authHeader).body()?.let { list ->
                    vehicleTypeDao.upsertAll(list.map { v -> VehicleTypeEntity(
                        id = v.id, name = v.name, iconKey = v.iconKey, sortOrder = v.sortOrder
                    )})
                }
                api.getCompetitorBrands(apiKey = apiKey, auth = authHeader).body()?.let { list ->
                    competitorBrandDao.upsertAll(list.map { b -> CompetitorBrandEntity(
                        id = b.id, name = b.name
                    )})
                }

                // Notices
                api.getNotices(apiKey = apiKey, auth = authHeader).body()?.let { list ->
                    noticeDao.upsertAll(list.map { n -> NoticeEntity(
                        id = n.id, message = n.message,
                        targetBaIds = n.targetBaIds, isActive = n.isActive,
                        postedAt = System.currentTimeMillis()
                    )})
                }

                // Messages
                api.getMessages(
                    filter = "sender_id.eq.$baId,receiver_id.eq.$baId",
                    apiKey = apiKey, auth = authHeader
                ).body()?.let { list ->
                    messageDao.upsertAll(list.map { m -> MessageEntity(
                        id = m.id, senderId = m.senderId, senderName = m.senderName,
                        receiverId = m.receiverId, body = m.body,
                        createdAt = System.currentTimeMillis(),
                        isRead = m.isRead, isOutgoing = m.senderId == baId
                    )})
                }

                // Payouts
                api.getPayouts(baId = "eq.$baId", apiKey = apiKey, auth = authHeader).body()?.let { list ->
                    payoutDao.upsertAll(list.map { p -> PayoutEntity(
                        id = p.id, baId = p.baId,
                        payoutDate = p.payoutDate ?: "",
                        amount     = p.amount,
                        note       = null,
                        createdAt  = System.currentTimeMillis()
                    )})
                }

                session.updateLastSync()
            } catch (_: Exception) { /* offline ? use cached data */ }
        }
    }
}
