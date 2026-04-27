package com.autoexpert.app.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoexpert.app.BuildConfig
import com.autoexpert.app.data.local.dao.BrandAmbassadorDao
import com.autoexpert.app.data.local.entity.BrandAmbassadorEntity
import com.autoexpert.app.data.remote.api.SupabaseApi
import com.autoexpert.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    private val baDao: BrandAmbassadorDao,
    private val api: SupabaseApi,
    private val session: SessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    val pin = MutableStateFlow("")
    val isAlreadyLoggedIn = MutableStateFlow(false)

    private val apiKey = BuildConfig.SUPABASE_ANON_KEY
    private val authHeader = "Bearer $apiKey"

    init {
        viewModelScope.launch {
            session.baId.collect { id ->
                isAlreadyLoggedIn.value = !id.isNullOrEmpty()
            }
        }
    }

    fun appendPin(digit: String) {
        if (pin.value.length < 6) pin.value += digit
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
                android.util.Log.d("AutoExpert", "PIN attempt: " + pin.value)
                // 1. Always try network first to get fresh station name
                var ba: com.autoexpert.app.data.local.entity.BrandAmbassadorEntity? = null
                if (true) {
                    val resp = api.getBaByPin(
                        appPin = "eq.${pin.value}",
                        apiKey = apiKey,
                        auth   = authHeader
                    )
                    android.util.Log.d("AutoExpert", "API response code: " + resp.code() + " body: " + resp.body()?.size)
                if (resp.isSuccessful) {
                        val remote = resp.body()?.firstOrNull()
                        if (remote != null) {
                            ba = BrandAmbassadorEntity(
                                id                   = remote.id,
                                name                 = remote.name,
                                cnic                 = remote.cnic,
                                stationId            = remote.stationId,
                                stationName          = null,
                                appPin               = remote.appPin,
                                isActive             = remote.isActive,
                                employmentType       = remote.employmentType,
                                currentMonthlySalary = remote.currentMonthlySalary,
                                joinedAt             = remote.joinedAt,
                                leaveAnnualLimit     = remote.leaveAnnualLimit,
                                leaveCasualLimit     = remote.leaveCasualLimit,
                                leaveSickLimit       = remote.leaveSickLimit,
                            )
                            baDao.upsert(ba)
                        }
                    }
                }

                if (ba != null) {
                    // Fetch station GPS coords
                    val stationsResp = api.getStations(apiKey = apiKey, auth = authHeader)
                    val station = stationsResp.body()?.find { it.id == ba.stationId }

                    session.saveSession(
                        baId        = ba.id,
                        baName      = ba.name,
                        stationId   = ba.stationId,
                        stationName = station?.name ?: "Station ${ba.stationId?.take(8) ?: "Unknown"}",
                        lat         = station?.latitude,
                        lng         = station?.longitude,
                        radius      = station?.geofenceRadius ?: 200
                    )
                    _state.value = LoginState.Success(ba)
                } else {
                    pin.value = ""
                    _state.value = LoginState.Error("Incorrect PIN. Please try again.")
                }
            } catch (e: Exception) {
                android.util.Log.e("AutoExpert", "Login error: " + e.javaClass.simpleName + ": " + e.message)
                _state.value = LoginState.Error("Error: " + e.javaClass.simpleName + ": " + e.message)
                pin.value = ""
            }
        }
    }

    fun sendPinResetRequest() {
        viewModelScope.launch {
            try {
                val baName = session.baName.first() ?: "Unknown BA"
                api.postMessage(
                    body = mapOf(
                        "sender_id"   to "system",
                        "sender_name" to baName,
                        "receiver_id" to "admin",
                        "body"        to "🔑 PIN Reset Request from $baName. Please reset their PIN."
                    ),
                    apiKey = apiKey, auth = authHeader
                )
                _state.value = LoginState.ResetSent
            } catch (e: Exception) {
                _state.value = LoginState.Error("Could not send request. Check connectivity.")
            }
        }
    }

    fun saveBiometricForCurrentPin(context: Context) {
        context.getSharedPreferences("bio_prefs", android.content.Context.MODE_PRIVATE)
            .edit().putString("enrolled_pin", pin.value).apply()
    }

    fun getBiometricPin(context: Context): String? {
        return context.getSharedPreferences("bio_prefs", android.content.Context.MODE_PRIVATE)
            .getString("enrolled_pin", null)
    }
}
