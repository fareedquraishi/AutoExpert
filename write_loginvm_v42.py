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
        if (pin.value.length < 6) {
            pin.value = pin.value + d
        }
        if (pin.value.length == 6) verifyPin()
    }

    fun deletePin() {
        if (pin.value.isNotEmpty()) pin.value = pin.value.dropLast(1)
        _state.value = LoginState.Idle
    }

    fun clearPin() {
        pin.value = ""
        _state.value = LoginState.Idle
    }

    private fun verifyPin() {
        viewModelScope.launch {
            _state.value = LoginState.Checking
            try {
                // Fetch BA from Supabase
                val resp = api.getBaByPin(
                    appPin = "eq." + pin.value,
                    apiKey = apiKey,
                    auth   = authHeader
                )

                if (!resp.isSuccessful || resp.body().isNullOrEmpty()) {
                    pin.value = ""
                    _state.value = LoginState.Error("Incorrect PIN. Please try again.")
                    return@launch
                }

                val remote = resp.body()!!.first()

                // Build entity safely using local variables (no dot-access in string templates)
                val baId         = remote.id
                val baName       = remote.name
                val baStationId  = remote.stationId ?: ""
                val baCnic       = remote.cnic
                val baPin        = remote.appPin
                val baActive     = remote.isActive
                val baEmpType    = remote.employmentType
                val baSalary     = remote.currentMonthlySalary
                val baJoined     = remote.joinedAt
                val baAnnual     = remote.leaveAnnualLimit
                val baCasual     = remote.leaveCasualLimit
                val baSick       = remote.leaveSickLimit

                val ba = BrandAmbassadorEntity(
                    id                   = baId,
                    name                 = baName,
                    cnic                 = baCnic,
                    stationId            = baStationId,
                    stationName          = null,
                    appPin               = baPin,
                    isActive             = baActive,
                    employmentType       = baEmpType,
                    currentMonthlySalary = baSalary,
                    joinedAt             = baJoined,
                    leaveAnnualLimit     = baAnnual,
                    leaveCasualLimit     = baCasual,
                    leaveSickLimit       = baSick,
                )
                baDao.upsert(ba)

                // Fetch station name safely
                var stationName   = "Station"
                var stationLat    : Double? = null
                var stationLng    : Double? = null
                var stationRadius = 200

                if (baStationId.isNotEmpty()) {
                    try {
                        val stResp = api.getStationById(
                            id     = "eq." + baStationId,
                            apiKey = apiKey,
                            auth   = authHeader
                        )
                        val st = stResp.body()?.firstOrNull()
                        if (st != null) {
                            val stName = st.name
                            val stCity = st.city ?: ""
                            stationName   = if (stCity.isNotEmpty()) "$stName, $stCity" else stName
                            stationLat    = st.latitude
                            stationLng    = st.longitude
                            stationRadius = st.geofenceRadius ?: 200
                        }
                    } catch (ex: Exception) {
                        android.util.Log.e("AutoExpert", "Station fetch error: " + ex.message)
                    }
                }

                // Save session safely using local variables only
                session.saveSession(
                    baId        = baId,
                    baName      = baName,
                    stationId   = baStationId,
                    stationName = stationName,
                    lat         = stationLat,
                    lng         = stationLng,
                    radius      = stationRadius
                )

                _state.value = LoginState.Success(ba)

            } catch (e: Exception) {
                android.util.Log.e("AutoExpert", "Login error: " + e.javaClass.simpleName + ": " + e.message)
                _state.value = LoginState.Error("Connection error. Check your internet.")
            }
        }
    }

    fun sendPinResetRequest() {
        _state.value = LoginState.ResetSent
    }
}
"""

dst = "app/src/main/java/com/autoexpert/app/ui/login/LoginViewModel.kt"
with open(dst, "w", encoding="ascii", errors="replace") as f:
    f.write(content)
print(f"Written {content.count(chr(10))} lines to {dst}")
print("No http:// in file:", "http://" not in content)
