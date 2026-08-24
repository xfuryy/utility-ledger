package com.example.utilityledger.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    private val currencyKey = stringPreferencesKey("currency")
    private val emailKey = stringPreferencesKey("reminder_email")

    val currency = context.dataStore.data.map { it[currencyKey] ?: "Rs." }
    val reminderEmail = context.dataStore.data.map { it[emailKey] ?: "" }

    suspend fun setCurrency(value: String) {
        context.dataStore.edit { it[currencyKey] = value }
    }

    suspend fun setReminderEmail(value: String) {
        context.dataStore.edit { it[emailKey] = value }
    }
}
