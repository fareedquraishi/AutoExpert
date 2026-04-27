package com.autoexpert.app.ui.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoexpert.app.BuildConfig
import com.autoexpert.app.data.local.dao.*
import com.autoexpert.app.data.local.entity.*
import com.autoexpert.app.data.remote.api.SupabaseApi
import com.autoexpert.app.data.remote.model.*
import com.autoexpert.app.util.SessionManager
import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
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
            s.copy(isApplicator = v, applicatorSkuId = if (!v) null else autoSkuId ?: s.applicatorSkuId)
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
            val baId = session.baId.first() ?: run {
                _state.update { it.copy(isSubmitting = false, submitError = "Not logged in") }
                return@launch
            }
            val stationId = session.stationId.first() ?: ""
            val commission = totalCommission
            val apiKey = BuildConfig.SUPABASE_ANON_KEY
            val authHdr = "Bearer $apiKey"

            try {
                // Post entry directly to Supabase
                val entryPayload = RemoteSaleEntry(
                    baId             = baId,
                    stationId        = stationId,
                    customerName     = s.customerName.trim().split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) },
                    customerMobile   = s.mobile.trim().ifEmpty { "" },
                    plateNumber      = s.plateNumber.trim().uppercase().ifEmpty { null },
                    vehicleTypeId    = s.vehicleTypeId.ifEmpty { null },
                    isRepeat         = s.isRepeat,
                    competitorBrandId = s.competitorBrandId,
                    entryTime        = Instant.now().toString(),
                    syncedAt         = Instant.now().toString(),
                )

                val resp = api.postSaleEntry(entryPayload, apiKey, authHdr)

                if (resp.isSuccessful) {
                    val remoteId = resp.body()?.firstOrNull()?.id
                    // Post items
                    if (selectedItems.isNotEmpty() && remoteId != null) {
                        val itemPayloads = selectedItems.map { cartItem ->
                            val volL = cartItem.sku.volumeLitres * cartItem.qty
                            val isApp = s.isApplicator && (s.applicatorSkuId == cartItem.sku.id || selectedItems.size == 1)
                            RemoteSaleEntryItem(
                                entryId                = remoteId,
                                skuId                  = cartItem.sku.id,
                                qtyLitres              = volL,
                                hasApplicator          = isApp,
                                applicatorQty          = if (isApp) volL else null,
                                purchasePriceSnapshot  = cartItem.sku.purchasePrice,
                                sellingPriceSnapshot   = cartItem.sku.sellingPrice,
                                marginSnapshot         = cartItem.sku.marginPercent,
                                commissionRateSnapshot = _commissionRate.value.toString(),
                                commissionEarned       = volL * _commissionRate.value,
                            )
                        }
                        api.postSaleEntryItems(itemPayloads, apiKey, authHdr)
                    }
                    _state.update { it.copy(isSubmitting = false, submitSuccess = true, totalCommission = commission) }
                } else {
                    val err = resp.errorBody()?.string() ?: "HTTP ${resp.code()}"
                    _state.update { it.copy(isSubmitting = false, submitError = "Failed: $err") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false, submitError = e.javaClass.simpleName + ": " + e.message) }
            }
        }
    }

    fun reset() = viewModelScope.launch {
        loadCommissionRate()
        val skus    = skuDao.getAllActiveOnce()
        val vTypes  = vehicleTypeDao.getAllOnce()
        val cBrands = competitorBrandDao.getAllOnce()
        _state.update {
            it.copy(
                step = 1, customerName = "", mobile = "", plateNumber = "",
                vehicleTypeId = "", vehicleTypeName = "", isRepeat = false,
                competitorBrandId = null, competitorBrandName = null,
                isApplicator = false, applicatorSkuId = null,
                isSubmitting = false, submitError = null, submitSuccess = false,
                skus = skus, vehicleTypes = vTypes, competitorBrands = cBrands,
                cart = skus.associate { s -> s.id to CartItem(s) }
            )
        }
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
