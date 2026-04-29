package com.autoexpert.app.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoexpert.app.BuildConfig
import com.autoexpert.app.data.local.dao.BrandAmbassadorDao
import com.autoexpert.app.data.local.entity.BrandAmbassadorEntity
import com.autoexpert.app.data.remote.api.SupabaseApi
import com.autoexpert.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    object Idle : LoginState()
    object Checking : LoginState()
    data class Success(val ba: BrandAmbassadorEntity) : LoginState()
    data class Error(val message: String) : LoginState()
    object ResetSent : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: SupabaseApi,
    private val baDao: BrandAmbassadorDao,
    private val session: SessionManager,
) : ViewModel() {

    val pin = mutableStateOf("")
    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    private val apiKey = BuildConfig.SUPABASE_ANON_KEY
    private val authHeader = "Bearer " + apiKey

    fun addDigit(d: String) { if (pin.value.length < 6) pin.value += d }
    fun deletePin() { if (pin.value.isNotEmpty()) pin.value = pin.value.dropLast(1) }
    fun clearPin() { pin.value = "" }

    fun verifyPin() {
        if (pin.value.length < 6) return
        viewModelScope.launch {
            _state.value = LoginState.Checking
            try {
                // FIXED: Passing apiKey and authHeader
                val resp = api.getBaByPin(appPin = "eq." + pin.value, apiKey = apiKey, auth = authHeader)
                val remote = resp.body()?.firstOrNull()
                
                if (remote == null) {
                    _state.value = LoginState.Error("Invalid PIN")
                    return@launch
                }

                val baStationId = remote.stationId ?: ""
                var stationName = "Station"
                var stLat: Double? = null
                var stLng: Double? = null
                var stRad = 200

                if (baStationId.isNotEmpty()) {
                    try {
                        // FIXED: Passing apiKey and authHeader
                        val stResp = api.getStationById(id = "eq." + baStationId, apiKey = apiKey, auth = authHeader)
                        stResp.body()?.firstOrNull()?.let { st ->
                            stationName = if (!st.city.isNullOrEmpty()) "${st.name}, ${st.city}" else st.name
                            stLat = st.latitude
                            stLng = st.longitude
                            stRad = st.geofenceRadius ?: 200
                        }
                    } catch (e: Exception) {}
                }

                val ba = BrandAmbassadorEntity(
                    id = remote.id,
                    name = remote.name,
                    stationId = baStationId,
                    stationName = stationName,
                    cnic = remote.cnic ?: "",
                    appPin = remote.appPin ?: "",
                    isActive = remote.isActive ?: true,
                    employmentType = remote.employmentType,
                    currentMonthlySalary = remote.currentMonthlySalary,
                    joinedAt = remote.joinedAt,
                    leaveAnnualLimit = remote.leaveAnnualLimit ?: 0,
                    leaveCasualLimit = remote.leaveCasualLimit ?: 0,
                    leaveSickLimit = remote.leaveSickLimit ?: 0
                )
                
                baDao.upsert(ba)
                session.saveSession(
                    baId        = remote.id,
                    baName      = remote.name,
                    stationId   = baStationId,
                    stationName = stationName,
                    lat         = stLat,
                    lng         = stLng,
                    radius      = stRad
                )
                _state.value = LoginState.Success(ba)

            } catch (e: Exception) {
                _state.value = LoginState.Error("Connection error")
            }
        }
    }

    fun sendPinResetRequest() { _state.value = LoginState.ResetSent }
}