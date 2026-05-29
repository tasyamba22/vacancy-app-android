package com.example.vacancyapp.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tokenDataStore by preferencesDataStore("token_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val ROLE_KEY = stringPreferencesKey("user_role")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val EMAIL_KEY = stringPreferencesKey("user_email")
    }

    val token: Flow<String?> = context.tokenDataStore.data.map { it[TOKEN_KEY] }
    val role: Flow<String?> = context.tokenDataStore.data.map { it[ROLE_KEY] }
    val userId: Flow<Int?> = context.tokenDataStore.data.map { it[USER_ID_KEY]?.toIntOrNull() }
    val email: Flow<String?> = context.tokenDataStore.data.map { it[EMAIL_KEY] }
    suspend fun saveToken(token: String, role: String, userId: Int, email: String) {
        context.tokenDataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[ROLE_KEY] = role
            prefs[USER_ID_KEY] = userId.toString()
            prefs[EMAIL_KEY] = email
        }
    }

    suspend fun clearToken() {
        context.tokenDataStore.edit { it.clear() }
    }
}