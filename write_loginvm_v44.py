content = """\
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
    object Idle     : LoginState()
    object Checking : LoginState()
    data class Success(val ba: BrandAmbassadorEntity) : LoginState()
    data class Error(val message: String) : LoginState()
    object ResetSent : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api    : SupabaseApi,
    private val baDao  : BrandAmbassadorDao,
    private val session: SessionManager,
) : ViewModel() {

    val pin   = mutableStateOf("")
    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state : StateFlow<LoginState> = _state

    private val apiKey     = BuildConfig.SUPABASE_ANON_KEY
    private val authHeader = "Bearer " + apiKey

    fun addDigit(d: String) {
        if (pin.value.length < 6) pin.value += d
    }

    fun clearPin() {
        pin.value = ""
    }

    fun verifyPin() {
        if (pin.value.length < 6) return
        
        viewModelScope.launch {
            _state.value = LoginState.Checking
            try {
                val appPin = "eq." + pin.value
                val resp = api.getBaByPin(appPin, apiKey, authHeader)
                
                if (resp.body().isNullOrEmpty()) {
                    _state.value = LoginState.Error("Invalid PIN")
                    return@launch
                }

                val remote = resp.body()!!.first()
                val baStationId = remote.stationId ?: ""
                
                var stationName = "Station"
                var stationLat: Double? = null
                var stationLng: Double? = null
                var stationRadius = 200

                if (baStationId.isNotEmpty()) {
                    val stResp = api.getStationById("eq." + baStationId, apiKey, authHeader)
                    val st = stResp.body()?.firstOrNull()
                    if (st != null) {
                        val name = st.name ?: "Unknown"
                        val city = st.city ?: ""
                        stationName = if (city.isNotEmpty()) "$name, $city" else name
                        stationLat = st.latitude
                        stationLng = st.longitude
                        stationRadius = st.geofenceRadius ?: 200
                    }
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
                    baId = remote.id,
                    baName = remote.name,
                    stationId = baStationId,
                    stationName = stationName,
                    lat = stationLat,
                    lng = stationLng,
                    radius = stationRadius
                )

                _state.value = LoginState.Success(ba)

            } catch (e: Exception) {
                _state.value = LoginState.Error("Connection error.")
            }
        }
    }

    fun sendPinResetRequest() {
        _state.value = LoginState.ResetSent
    }
}
\"\"\"
import os
dst = "app/src/main/java/com/autoexpert/app/ui/login/LoginViewModel.kt"
os.makedirs(os.path.dirname(dst), exist_ok=True)
with open(dst, "w", encoding="ascii", errors="replace") as f:
    f.write(content)
print(f"Written to {dst}")
