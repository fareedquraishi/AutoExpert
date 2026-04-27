content = '''\
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
    val todayConversions: Int = 0,
    val reachTarget: Double = 100.0,
    val litresTarget: Double = 100.0,
    val unreadNotices: Int = 0,
    val unreadMessages: Int = 0,
    val attendanceThisMonth: Int = 0,
    val todayAttendanceMarked: Boolean = false,
    val todayCustomers: List<SaleEntryQueueEntity> = emptyList(),
    val syncError: String = "",
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val session: SessionManager,
    private val saleDao: SaleEntryQueueDao,
    private val noticeDao: NoticeDao,
    private val messageDao: MessageDao,
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
        loadLocalData()
        syncFromSupabase()
    }

    private fun loadLocalData() {
        // Single combined session collector
        viewModelScope.launch {
            combine(
                session.baName.map { it ?: "" },
                session.stationName.map { it ?: "" }
            ) { name, station -> name to station }
            .collect { (name, station) ->
                _uiState.update { it.copy(baName = name, stationName = station) }
            }
        }

        // Today's entries from Room
        viewModelScope.launch {
            session.baId.map { it ?: "" }.filter { it.isNotEmpty() }.flatMapLatest { baId ->
                saleDao.getByBaAndDate(baId, today)
            }.collect { entries ->
                _uiState.update { it.copy(
                    todayReach      = entries.size,
                    todayLitres     = entries.sumOf { it.totalLitres },
                    todayCommission = entries.sumOf { it.totalCommission },
                    todayConversions = entries.count { !it.isRepeat && it.totalLitres > 0 },
                    todayCustomers  = entries
                )}
            }
        }

        // Unread counts combined
        viewModelScope.launch {
            combine(
                noticeDao.getUnreadCount(),
                messageDao.getUnreadCount()
            ) { notices, messages -> notices to messages }
            .collect { (notices, messages) ->
                _uiState.update { it.copy(unreadNotices = notices, unreadMessages = messages) }
            }
        }

        // Attendance + targets
        viewModelScope.launch {
            session.baId.map { it ?: "" }.filter { it.isNotEmpty() }.collect { baId ->
                val presentDays = attendanceDao.countPresentDays(baId, monthPrefix)
                val att         = attendanceDao.getByBaAndDate(baId, today)
                val stationId   = session.stationId.first() ?: ""
                val target      = targetDao.getActiveTarget(baId, stationId, today)
                _uiState.update { it.copy(
                    attendanceThisMonth   = presentDays,
                    todayAttendanceMarked = att != null,
                    reachTarget  = if (target?.targetBasis == "reach")  target.targetValue else 100.0,
                    litresTarget = if (target?.targetBasis == "litres") target.targetValue else 100.0,
                )}
            }
        }
    }

    fun syncFromSupabase() {
        viewModelScope.launch {
            val baId = session.baId.first() ?: return@launch
            try {
                // Fix station name if missing
                val currentStation = session.stationName.first()
                if (currentStation.isNullOrEmpty() || currentStation == "Unknown Station") {
                    val stationId = session.stationId.first() ?: ""
                    if (stationId.isNotEmpty()) {
                        api.getStationById(
                            id = "eq.$stationId",
                            apiKey = apiKey, auth = authHeader
                        ).body()?.firstOrNull()?.let { st ->
                            session.saveSession(
                                baId        = baId,
                                baName      = session.baName.first() ?: "",
                                stationId   = stationId,
                                stationName = "${st.name}, ${st.city ?: ""}".trimEnd().trimEnd(','),
                                lat         = st.latitude,
                                lng         = st.longitude,
                                radius      = st.geofenceRadius ?: 200
                            )
                        }
                    }
                }

                // Sync reference data (SKUs, vehicles, brands)
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

                // Fetch today entries from Supabase - batch insert to avoid UI jumping
                val remoteEntries = api.getSaleEntries(
                    baId = "eq.$baId",
                    date = "gte.${today}T00:00:00",
                    select = "id,ba_id,station_id,customer_name,customer_mobile,plate_number,vehicle_type_id,is_repeat,entry_time,sale_entry_items(qty_litres,commission_earned)",
                    apiKey = apiKey, auth = authHeader
                ).body() ?: emptyList()

                // Batch insert all at once
                val entities = remoteEntries.mapNotNull { e ->
                    if (e.id == null) return@mapNotNull null
                    SaleEntryQueueEntity(
                        localId          = e.id,
                        remoteId         = e.id,
                        baId             = e.baId,
                        stationId        = e.stationId,
                        customerName     = e.customerName,
                        customerMobile   = e.customerMobile,
                        plateNumber      = e.plateNumber,
                        vehicleTypeId    = e.vehicleTypeId,
                        vehicleTypeName  = null,
                        isRepeat         = e.isRepeat,
                        entryTime        = e.entryTime,
                        syncStatus       = "synced",
                        totalLitres      = e.items?.sumOf { it.qtyLitres } ?: 0.0,
                        totalCommission  = e.items?.sumOf { it.commissionEarned } ?: 0.0,
                    )
                }
                if (entities.isNotEmpty()) {
                    saleDao.upsertAll(entities)
                }

                // Notices
                api.getNotices(apiKey = apiKey, auth = authHeader).body()?.let { list ->
                    noticeDao.upsertAll(list.map { n -> NoticeEntity(
                        id = n.id, message = n.message,
                        targetBaIds = n.targetBaIds?.joinToString(","),
                        isActive = n.isActive,
                        postedAt = System.currentTimeMillis()
                    )})
                }

                session.updateLastSync()
                _uiState.update { it.copy(syncError = "") }
            } catch (e: Exception) {
                _uiState.update { it.copy(syncError = e.javaClass.simpleName + ": " + (e.message ?: "?")) }
            }
        }
    }
}
'''

dst = "app/src/main/java/com/autoexpert/app/ui/home/HomeViewModel.kt"
with open(dst, "w", encoding="ascii", errors="replace") as f:
    f.write(content)
print(f"Written {content.count(chr(10))} lines to {dst}")
