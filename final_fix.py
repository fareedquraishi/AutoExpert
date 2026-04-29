import os

# 1. FIX VIEWMODEL
vm_path = "app/src/main/java/com/autoexpert/app/ui/login/LoginViewModel.kt"
vm_code = """package com.autoexpert.app.ui.login

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
    fun sendPinResetRequest() { _state.value = LoginState.ResetSent }

    fun verifyPin() {
        if (pin.value.length < 6) return
        viewModelScope.launch {
            _state.value = LoginState.Checking
            try {
                // FIXED: Passing all 3 required parameters to API
                val resp = api.getBaByPin("eq." + pin.value, apiKey, authHeader)
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
                        // FIXED: Passing all 3 required parameters to API
                        val stResp = api.getStationById("eq." + baStationId, apiKey, authHeader)
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
                session.saveSession(remote.id, remote.name, baStationId, stationName, stLat, stLng, stRad)
                _state.value = LoginState.Success(ba)

            } catch (e: Exception) {
                _state.value = LoginState.Error("Connection error")
            }
        }
    }
}"""

with open(vm_path, "w") as f: f.write(vm_code)

# 2. FIX LOGIN SCREEN
ui_path = "app/src/main/java/com/autoexpert/app/ui/login/LoginScreen.kt"
with open(ui_path, "r") as f:
    content = f.read()

# Fix Imports
if "import androidx.compose.runtime.getValue" not in content:
    content = content.replace("package com.autoexpert.app.ui.login", "package com.autoexpert.app.ui.login\n\nimport androidx.compose.runtime.getValue\nimport androidx.compose.runtime.collectAsState")

# Fix references to match ViewModel
content = content.replace("val state = viewModel.state.collectAsState()", "val state by viewModel.state.collectAsState()")
content = content.replace("viewModel.appendPin(", "viewModel.addDigit(")
content = content.replace("viewModel.deletePin()", "viewModel.deletePin()") # Ensure call matches
content = content.replace("pin.length", "viewModel.pin.value.length")
content = content.replace("viewModel.getBiometricPin()", "viewModel.pin.value")

with open(ui_path, "w") as f: f.write(content)

print("ViewModel and UI have been synchronized and fixed.")
