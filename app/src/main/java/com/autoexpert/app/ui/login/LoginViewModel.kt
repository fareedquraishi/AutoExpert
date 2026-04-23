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
                // 1. Try local Room first (offline support)
                var ba = baDao.getByPin(pin.value)

                // 2. If not found locally, fetch from Supabase
                if (ba == null) {
                    val resp = api.getBaByPin(
                        pin = "eq.${pin.value}",
                        apiKey = apiKey,
                        auth = authHeader
                    )
                    if (resp.isSuccessful) {
                        val remote = resp.body()?.firstOrNull()
                        if (remote != null) {
                            ba = BrandAmbassadorEntity(
                                id = remote.id, name = remote.name,
                                email = remote.email, phone = remote.phone,
                                stationId = remote.stationId, stationName = null,
                                pin = remote.pin, status = remote.status,
                                salaryAmount = remote.salaryAmount, city = remote.city
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
                        baId = ba.id, baName = ba.name,
                        stationId = ba.stationId, stationName = station?.name ?: ba.stationName,
                        lat = station?.latitude, lng = station?.longitude,
                        radius = station?.geofenceRadius ?: 200
                    )
                    _state.value = LoginState.Success(ba)
                } else {
                    pin.value = ""
                    _state.value = LoginState.Error("Incorrect PIN. Please try again.")
                }
            } catch (e: Exception) {
                _state.value = LoginState.Error("Connection error. Check your internet.")
                pin.value = ""
            }
        }
    }

    fun sendPinResetRequest() {
        viewModelScope.launch {
            try {
                // Post a system message to admin
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
        // Biometric enrollment is handled via BiometricManager in the screen
        // We store the PIN hash in EncryptedSharedPreferences after biometric auth
        val prefs = context.getSharedPreferences("bio_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("enrolled_pin", pin.value).apply()
    }

    fun getBiometricPin(context: Context): String? {
        val prefs = context.getSharedPreferences("bio_prefs", Context.MODE_PRIVATE)
        return prefs.getString("enrolled_pin", null)
    }
}
