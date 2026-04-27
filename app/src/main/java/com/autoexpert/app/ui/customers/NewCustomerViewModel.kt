package com.autoexpert.app.ui.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoexpert.app.BuildConfig
import com.autoexpert.app.data.local.dao.*
import com.autoexpert.app.data.local.entity.*
import com.autoexpert.app.data.remote.api.SupabaseApi
import com.autoexpert.app.service.SyncWorker
import com.autoexpert.app.util.SessionManager
import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class CartItem(val sku: SkuEntity, var qty: Int = 0)

data class CustomerEntryState(
    val step: Int = 1,
    val customerName: String = "",
    val mobile: String = "",
    val plateNumber: String = "",
    val vehicleTypeId: String = "",
    val vehicleTypeName: String = "",
    val isRepeat: Boolean = false,
    val competitorBrandId: String? = null,
    val competitorBrandName: String? = null,
    val skus: List<SkuEntity> = emptyList(),
    val cart: Map<String, CartItem> = emptyMap(),
    val isApplicator: Boolean = false,
    val applicatorSkuId: String? = null,
    val vehicleTypes: List<VehicleTypeEntity> = emptyList(),
    val competitorBrands: List<CompetitorBrandEntity> = emptyList(),
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false,
    val totalCommission: Double = 0.0,
)

@HiltViewModel
class NewCustomerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val session: SessionManager,
    private val skuDao: SkuDao,
    private val vehicleTypeDao: VehicleTypeDao,
    private val competitorBrandDao: CompetitorBrandDao,
    private val commissionPackageDao: CommissionPackageDao,
    private val commissionTierDao: CommissionTierDao,
    private val baCommissionOverrideDao: BaCommissionOverrideDao,
    private val saleQueueDao: SaleEntryQueueDao,
    private val api: SupabaseApi,
    private val gson: Gson,
) : ViewModel() {

    private val _state = MutableStateFlow(CustomerEntryState())
    val state: StateFlow<CustomerEntryState> = _state.asStateFlow()

    private val _commissionRate = MutableStateFlow(50.0)

    init { reset() }

    fun onNameChanged(v: String) = _state.update { it.copy(customerName = v) }
    fun onMobileChanged(v: String) = _state.update { it.copy(mobile = v) }
    fun onPlateChanged(v: String) = _state.update { it.copy(plateNumber = v) }
    fun onVehicleSelected(id: String, name: String) = _state.update { it.copy(vehicleTypeId = id, vehicleTypeName = name) }
    fun onRepeatToggle(v: Boolean) = _state.update { it.copy(isRepeat = v, competitorBrandId = if (v) null else it.competitorBrandId) }
    fun onBrandSelected(id: String, name: String) = _state.update { it.copy(competitorBrandId = id, competitorBrandName = name) }
    fun nextStep() = _state.update { it.copy(step = it.step + 1) }
    fun prevStep() = _state.update { it.copy(step = it.step - 1) }

    fun incrementQty(skuId: String) {
        _state.update { s ->
            val updated = s.cart.toMutableMap()
            val cur = updated[skuId]!!.qty
            updated[skuId] = updated[skuId]!!.copy(qty = cur + 1)
            s.copy(cart = updated)
        }
    }

    fun decrementQty(skuId: String) {
        _state.update { s ->
            val updated = s.cart.toMutableMap()
            val cur = updated[skuId]!!.qty
            if (cur > 0) updated[skuId] = updated[skuId]!!.copy(qty = cur - 1)
            s.copy(cart = updated)
        }
    }

    fun onApplicatorToggle(v: Boolean) {
        _state.update { s ->
            val items = s.cart.values.filter { it.qty > 0 }
            val autoSkuId = if (v && items.size == 1) items.first().sku.id else null
            s.copy(
                isApplicator = v,
                applicatorSkuId = if (!v) null else autoSkuId ?: s.applicatorSkuId
            )
        }
    }

    fun onApplicatorSkuSelected(skuId: String) = _state.update { it.copy(applicatorSkuId = skuId) }

    fun removeFromCart(skuId: String) {
        _state.update { s ->
            val newCart = s.cart.toMutableMap()
            newCart[skuId]?.let { newCart[skuId] = it.copy(qty = 0) }
            val hasItems = newCart.values.any { it.qty > 0 }
            s.copy(
                cart = newCart,
                isApplicator = if (!hasItems) false else s.isApplicator,
                applicatorSkuId = if (s.applicatorSkuId == skuId) null else s.applicatorSkuId
            )
        }
    }

    val selectedItems get() = _state.value.cart.values.filter { it.qty > 0 }
    val totalLitres get() = selectedItems.sumOf { item -> item.sku.volumeLitres * item.qty }
    val totalCommission get() = selectedItems.sumOf { item -> item.sku.volumeLitres * item.qty * _commissionRate.value }

    fun submitWithoutProduct() {
        _state.update { s -> s.copy(cart = s.cart.mapValues { (_, v) -> v.copy(qty = 0) }) }
        submit()
    }

    fun submit() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, submitError = null) }
            val s = _state.value
            val baId = session.baId.first() ?: return@launch
            val stationId = session.stationId.first() ?: ""
            val commission = totalCommission
            val litres = totalLitres

            val itemsList = selectedItems.map { cartItem ->
                val volL = cartItem.sku.volumeLitres * cartItem.qty
                val comm = volL * _commissionRate.value
                mapOf(
                    "skuId" to cartItem.sku.id,
                    "qtyLitres" to volL,
                    "purchasePriceSnapshot" to cartItem.sku.purchasePrice,
                    "sellingPriceSnapshot" to cartItem.sku.sellingPrice,
                    "marginSnapshot" to cartItem.sku.marginPercent,
                    "commissionRateSnapshot" to _commissionRate.value.toString(),
                    "commissionEarned" to comm,
                    "hasApplicator" to (s.isApplicator && (s.applicatorSkuId == cartItem.sku.id || selectedItems.size == 1)),
                    "applicatorQty" to if (s.isApplicator && (s.applicatorSkuId == cartItem.sku.id || selectedItems.size == 1)) volL else null
                )
            }

            val entity = SaleEntryQueueEntity(
                localId          = UUID.randomUUID().toString(),
                baId             = baId,
                stationId        = stationId,
                customerName     = s.customerName.trim().split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) },
                customerMobile   = s.mobile.trim().ifEmpty { null },
                plateNumber      = s.plateNumber.trim().uppercase().ifEmpty { null },
                vehicleTypeId    = s.vehicleTypeId.ifEmpty { null },
                vehicleTypeName  = s.vehicleTypeName.ifEmpty { null },
                isRepeat         = s.isRepeat,
                competitorBrandId = s.competitorBrandId,
                isApplicator     = s.isApplicator,
                applicatorSkuId  = s.applicatorSkuId,
                totalLitres      = litres,
                totalCommission  = commission,
                itemsJson        = gson.toJson(itemsList),
                entryTime        = java.time.Instant.now().toString(),
                syncStatus       = "pending",
            )
            saleQueueDao.insert(entity)
            // Direct sync to Supabase
            try {
                val apiKey = com.autoexpert.app.BuildConfig.SUPABASE_ANON_KEY
                val authHdr = "Bearer " + apiKey
                val remote = com.autoexpert.app.data.remote.model.RemoteSaleEntry(
                    baId = entity.baId,
                    stationId = entity.stationId,
                    customerName = entity.customerName,
                    customerMobile = entity.customerMobile ?: "",
                    plateNumber = entity.plateNumber,
                    vehicleTypeId = entity.vehicleTypeId,
                    isRepeat = entity.isRepeat,
                    competitorBrandId = entity.competitorBrandId,
                    isApplicator = entity.isApplicator,
                    applicatorSkuId = entity.applicatorSkuId,
                    entryTime = entity.entryTime,
                )
                val resp = api.postSaleEntry(remote, apiKey, authHdr)
                if (resp.isSuccessful) {
                    val remoteId = resp.body()?.firstOrNull()?.id
                    saleQueueDao.updateSyncStatus(entity.localId, "synced", remoteId)
                }
            } catch (e: Exception) {
                SyncWorker.triggerImmediateSync(context)
            }
            _state.update { it.copy(isSubmitting = false, submitSuccess = true, totalCommission = commission) }
        }
    }

    fun reset() = viewModelScope.launch {
        loadCommissionRate()
        val skus   = skuDao.getAllActiveOnce()
        val vTypes = vehicleTypeDao.getAllOnce()
        val cBrands = competitorBrandDao.getAllOnce()
        _state.update { it.copy(
            step = 1, customerName = "", mobile = "", plateNumber = "",
            vehicleTypeId = "", vehicleTypeName = "", isRepeat = false,
            competitorBrandId = null, competitorBrandName = null,
            isApplicator = false, applicatorSkuId = null,
            isSubmitting = false, submitError = null, submitSuccess = false,
            skus = skus, vehicleTypes = vTypes, competitorBrands = cBrands,
            cart = skus.associate { s -> s.id to CartItem(s) }
        )}
    }

    private suspend fun loadCommissionRate() {
        try {
            val baId = session.baId.first()
            val today = java.time.LocalDate.now().toString()
            val rate: Double? = if (baId != null) {
                val override = baCommissionOverrideDao.getActiveForBa(baId, today)
                if (override != null) {
                    val tiers = commissionTierDao.getForPackage(override.packageId)
                    tiers.firstOrNull()?.rate ?: commissionPackageDao.getById(override.packageId)?.flatRate
                } else {
                    val pkg = commissionPackageDao.getGlobalActive()
                    if (pkg != null) {
                        val tiers = commissionTierDao.getForPackage(pkg.id)
                        tiers.firstOrNull()?.rate ?: pkg.flatRate
                    } else null
                }
            } else null
            _commissionRate.value = rate ?: 50.0
        } catch (e: Exception) {
            _commissionRate.value = 50.0
        }
    }
}
