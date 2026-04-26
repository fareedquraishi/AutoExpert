package com.autoexpert.app.util

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = "session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val BA_ID       = stringPreferencesKey("ba_id")
        val BA_NAME     = stringPreferencesKey("ba_name")
        val BA_STATION_ID   = stringPreferencesKey("station_id")
        val BA_STATION_NAME = stringPreferencesKey("station_name")
        val STATION_LAT = floatPreferencesKey("station_lat")
        val STATION_LNG = floatPreferencesKey("station_lng")
        val STATION_RADIUS = intPreferencesKey("station_radius")
        val FCM_TOKEN   = stringPreferencesKey("fcm_token")
        val LAST_SYNC   = longPreferencesKey("last_sync")
    }

    val baId       = context.dataStore.data.map { it[BA_ID] }
    val baName     = context.dataStore.data.map { it[BA_NAME] }
    val stationId  = context.dataStore.data.map { it[BA_STATION_ID] }
    val stationName= context.dataStore.data.map { it[BA_STATION_NAME] }
    val stationLat = context.dataStore.data.map { it[STATION_LAT]?.toDouble() }
    val stationLng = context.dataStore.data.map { it[STATION_LNG]?.toDouble() }
    val stationRadius = context.dataStore.data.map { it[STATION_RADIUS] ?: 200 }

    suspend fun saveSession(
        baId: String, baName: String,
        stationId: String?, stationName: String?,
        lat: Double? = null, lng: Double? = null, radius: Int = 200
    ) {
        context.dataStore.edit { prefs ->
            prefs[BA_ID]           = baId
            prefs[BA_NAME]         = baName
            prefs[BA_STATION_ID]   = stationId ?: ""
            prefs[BA_STATION_NAME] = stationName ?: ""
            lat?.let { prefs[STATION_LAT] = it.toFloat() }
            lng?.let { prefs[STATION_LNG] = it.toFloat() }
            prefs[STATION_RADIUS]  = radius
        }
    }

    suspend fun saveFcmToken(token: String) {
        context.dataStore.edit { it[FCM_TOKEN] = token }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun updateLastSync() {
        context.dataStore.edit { it[LAST_SYNC] = System.currentTimeMillis() }
    }
}
