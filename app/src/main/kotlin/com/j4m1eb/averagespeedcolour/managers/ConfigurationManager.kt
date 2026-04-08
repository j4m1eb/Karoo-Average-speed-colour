package com.j4m1eb.averagespeedcolour.managers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.j4m1eb.averagespeedcolour.data.ConfigData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ConfigurationManager(
    private val context: Context,
) {
    companion object {
        private val TARGET_SPEED_KEY = doublePreferencesKey("targetSpeed")
        private val USE_TEAL_KEY = booleanPreferencesKey("useTeal")
        private val SHOW_ICONS_KEY = booleanPreferencesKey("showIcons")
        private val SEEN_WELCOME_KEY = booleanPreferencesKey("hasSeenWelcome")
        private val SWAP_ROWS_KEY = booleanPreferencesKey("swapRows")
    }

    suspend fun saveConfig(config: ConfigData) {
        Timber.d("Attempting to save configuration to DataStore: $config")
        context.dataStore.edit { preferences ->
            preferences[TARGET_SPEED_KEY] = config.targetSpeed
            preferences[USE_TEAL_KEY] = config.useTeal
            preferences[SHOW_ICONS_KEY] = config.showIcons
            preferences[SEEN_WELCOME_KEY] = config.hasSeenWelcome
            preferences[SWAP_ROWS_KEY] = config.swapRows
        }
        Timber.i("Configuration successfully saved to DataStore.")
    }

    suspend fun getConfig(): ConfigData {
        Timber.d("Attempting to retrieve configuration from DataStore.")
        return context.dataStore.data.map { preferences ->
            val config = ConfigData(
                targetSpeed = preferences[TARGET_SPEED_KEY] ?: ConfigData.DEFAULT.targetSpeed,
                useTeal = preferences[USE_TEAL_KEY] ?: ConfigData.DEFAULT.useTeal,
                showIcons = preferences[SHOW_ICONS_KEY] ?: ConfigData.DEFAULT.showIcons,
                hasSeenWelcome = preferences[SEEN_WELCOME_KEY] ?: ConfigData.DEFAULT.hasSeenWelcome,
                swapRows = preferences[SWAP_ROWS_KEY] ?: ConfigData.DEFAULT.swapRows,
            )
            Timber.d("Retrieved configuration: $config")
            config
        }.first()
    }

    fun getConfigFlow(): Flow<ConfigData> {
        return context.dataStore.data.map { preferences ->
            ConfigData(
                targetSpeed = preferences[TARGET_SPEED_KEY] ?: ConfigData.DEFAULT.targetSpeed,
                useTeal = preferences[USE_TEAL_KEY] ?: ConfigData.DEFAULT.useTeal,
                showIcons = preferences[SHOW_ICONS_KEY] ?: ConfigData.DEFAULT.showIcons,
                hasSeenWelcome = preferences[SEEN_WELCOME_KEY] ?: ConfigData.DEFAULT.hasSeenWelcome,
                swapRows = preferences[SWAP_ROWS_KEY] ?: ConfigData.DEFAULT.swapRows,
            )
        }.distinctUntilChanged()
    }
}
