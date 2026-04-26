package com.autoexpert.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoexpert.app.BuildConfig
import com.autoexpert.app.data.local.dao.*
import com.autoexpert.app.data.local.entity.SaleEntryQueueEntity
import com.autoexpert.app.data.remote.api.SupabaseApi
import com.autoexpert.app.data.remote.model.*
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
    val reachTarget: Double = 0.0,
    val litresTarget: Double = 0.0,
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
    private val api: SupabaseApi,
) : ViewModel() {

    private val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private val monthPrefix = today.substring(0, 7)
    private val apiKey = BuildConfig.SUPABASE_ANON_KEY
    private val auth = "Bearer $apiKey"

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadData()
        syncRemoteData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                session.baName.filterNotNull(),
                session.stationName.filterNotNull(),
            ) { name, station -> name to station }
                .collect { (name, station) ->
                    _uiState.update { it.copy(baName = name, stationName = station) }
                }
        }

        viewModelScope.launch {
            session.baId.filterNotNull().collect { baId ->
                // Today's entries
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

        viewModelScope.launch {
            session.baId.filterNotNull().collect { baId ->
                // unpaid balance now calculated from sale_entry commission minus paid amounts
                val unpaid = payoutDao.getTotalUnpaid(baId) ?: 0.0
                val presentDays = attendanceDao.countPresentDays(baId, monthPrefix)
                val att = attendanceDao.getByBaAndDate(baId, today)

                // Target
                val stationId = session.stationId.first() ?: ""
                val target = targetDao.getActiveTarget(baId, stationId, today)

                _uiState.update { it.copy(
                    unpaidBalance         = unpaid,
                    attendanceThisMonth   = presentDays,
                    todayAttendanceMarked = att != null,
                    reachTarget  = if (target?.targetBasis == "reach")  target.targetValue else 20.0,
                    litresTarget = if (target?.targetBasis == "litres") target.targetValue else 100.0,
                )}
            }
        }
    }

    fun syncRemoteData() {
        viewModelScope.launch {
            val baId = session.baId.first() ?: return@launch
            try {
                // Sync notices
                api.getNotices(apiKey = apiKey, auth = auth).body()?.let { notices ->
                    val entities = notices.map { n ->
                        com.autoexpert.app.data.local.entity.NoticeEntity(
                            id = n.id, message = n.message,
                            targetBaIds = n.targetBaIds, isActive = n.isActive,
                            postedAt = System.currentTimeMillis()
                        )
                    }
                    noticeDao.upsertAll(entities)
                }
                // Sync messages
                api.getMessages(
                    filter = "(sender_id.eq.$baId,receiver_id.eq.$baId)",
                    apiKey = apiKey, auth = auth
                ).body()?.let { msgs ->
                    val entities = msgs.map { m ->
                        com.autoexpert.app.data.local.entity.MessageEntity(
                            id = m.id, senderId = m.senderId, senderName = m.senderName,
                            receiverId = m.receiverId, body = m.body,
                            createdAt = try { java.time.Instant.parse(m.createdAt).toEpochMilli() } catch(_:Exception){ System.currentTimeMillis() },
                            isRead = m.isRead, isOutgoing = m.senderId == baId
                        )
                    }
                    messageDao.upsertAll(entities)
                }
                // Sync payouts
                api.getPayouts(baId = "eq.$baId", apiKey = apiKey, auth = auth).body()?.let { payouts ->
                    val entities = payouts.map { p ->
                        com.autoexpert.app.data.local.entity.PayoutEntity(
                            id = p.id, baId = p.baId,
                            payoutDate = p.payoutDate,
                            amount = p.amount,
                            note = p.note
                        )
                    }
                    payoutDao.upsertAll(entities)
                }
                session.updateLastSync()
            } catch (_: Exception) { /* offline — use cached data */ }
        }
    }
}
