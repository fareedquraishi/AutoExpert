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

data class CartItem(
    val sku: SkuEntity,
    var qty: Int = 0
)

data class CustomerEntryState(
    val step: Int = 1,
    // Step 1
    val customerName: String = "",
    val mobile: String = "",
    val plateNumber: String = "",
    val vehicleTypeId: String = "",
    val vehicleTypeName: String = "",
    val isRepeat: Boolean = false,
    val competitorBrandId: String? = null,
    val competitorBrandName: String? = null,
    // Step 2
    val skus: List<SkuEntity> = emptyList(),
    val cart: Map<String, CartItem> = emptyMap(),
    val isApplicator: Boolean = false,
    val vehicleTypes: List<VehicleTypeEntity> = emptyList(),
    val competitorBrands: List<CompetitorBrandEntity> = emptyList(),
    // Step 3
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false,
    val totalCommission: Double = 0.0,
)

@HiltViewModel
class NewCustomerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val session: SessionManager,
    private val saleQueueDao: SaleEntryQueueDao,
    private val skuDao: SkuDao,
    private val vehicleTypeDao: VehicleTypeDao,
    private val competitorBrandDao: CompetitorBrandDao,
    private val api: SupabaseApi,
    private val gson: Gson,
) : ViewModel() {

    private val _state = MutableStateFlow(CustomerEntryState())
    val state: StateFlow<CustomerEntryState> = _state

    init {
        viewModelScope.launch {
            val skus     = skuDao.getAllActiveOnce()
            val vTypes   = vehicleTypeDao.getAllOnce()
            val cBrands  = competitorBrandDao.getAllOnce()
            _state.update { it.copy(
                skus = skus,
                vehicleTypes = vTypes,
                competitorBrands = cBrands,
                cart = skus.associate { it.id to CartItem(it) }
            )}
        }
    }

    // Step 1 updates
    fun onNameChanged(v: String)  = _state.update { it.copy(customerName = v) }
    fun onMobileChanged(v: String)= _state.update { it.copy(mobile = v) }
    fun onPlateChanged(v: String) = _state.update { it.copy(plateNumber = v) }
    fun onVehicleSelected(id: String, name: String) = _state.update { it.copy(vehicleTypeId = id, vehicleTypeName = name) }
    fun onRepeatToggle(v: Boolean)= _state.update { it.copy(isRepeat = v, competitorBrandId = if (v) null else it.competitorBrandId) }
    fun onBrandSelected(id: String, name: String) = _state.update { it.copy(competitorBrandId = id, competitorBrandName = name) }

    fun nextStep() = _state.update { it.copy(step = it.step + 1) }
    fun prevStep() = _state.update { it.copy(step = it.step - 1) }

    // Step 2 updates
    fun incrementQty(skuId: String) {
        _state.update { s ->
            val updated = s.cart.toMutableMap()
            updated[skuId] = updated[skuId]!!.copy(qty = updated[skuId]!!.qty + 1)
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
    fun onApplicatorToggle(v: Boolean) = _state.update { it.copy(isApplicator = v) }

    val selectedItems get() = _state.value.cart.values.filter { it.qty > 0 }
    val totalLitres   get() = selectedItems.sumOf { it.sku.volumeLitres * it.qty }
    val totalCommission get() = selectedItems.sumOf { it.sku.margin * it.qty }

    // Submit
    fun submit() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, submitError = null) }
            val s = _state.value
            val baId      = session.baId.first() ?: return@launch
            val stationId = session.stationId.first() ?: ""
            val today     = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Serialize cart items
            val itemsList = selectedItems.map { cartItem ->
                mapOf(
                    "skuId"      to cartItem.sku.id,
                    "qtyPacks"   to cartItem.qty.toDouble(),
                    "qtyLitres"  to (cartItem.sku.volumeLitres * cartItem.qty),
                    "unitPrice"  to cartItem.sku.sellingPrice,
                    "commission" to (cartItem.sku.margin * cartItem.qty)
                )
            }

            val entity = SaleEntryQueueEntity(
                localId          = UUID.randomUUID().toString(),
                baId             = baId,
                stationId        = stationId,
                customerName     = s.customerName.trim().split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) },
                mobile           = s.mobile.trim().ifEmpty { null },
                plateNumber      = s.plateNumber.trim().uppercase().ifEmpty { null },
                vehicleTypeId    = s.vehicleTypeId.ifEmpty { null },
                vehicleTypeName  = s.vehicleTypeName.ifEmpty { null },
                isRepeat         = s.isRepeat,
                competitorBrandId= s.competitorBrandId,
                isApplicator     = s.isApplicator,
                totalLitres      = totalLitres,
                totalCommission  = totalCommission,
                entryDate        = today,
                itemsJson        = gson.toJson(itemsList),
                syncStatus       = "pending"
            )

            saleQueueDao.insert(entity)
            SyncWorker.triggerImmediateSync(context)
            _state.update { it.copy(isSubmitting = false, submitSuccess = true, totalCommission = totalCommission) }
        }
    }

    fun reset() = _state.update { CustomerEntryState() }.also {
        viewModelScope.launch {
            val skus   = skuDao.getAllActiveOnce()
            val vTypes = vehicleTypeDao.getAllOnce()
            val cBrands= competitorBrandDao.getAllOnce()
            _state.update { it.copy(
                skus = skus, vehicleTypes = vTypes, competitorBrands = cBrands,
                cart = skus.associate { s -> s.id to CartItem(s) }
            )}
        }
    }
}
