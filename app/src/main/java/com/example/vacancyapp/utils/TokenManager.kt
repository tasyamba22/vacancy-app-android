package com.example.vacancyapp.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tokenDataStore by preferencesDataStore("token_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val ROLE_KEY = stringPreferencesKey("user_role")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val EMAIL_KEY = stringPreferencesKey("user_email")
    }

    val token: Flow<String?> = context.tokenDataStore.data.map { it[TOKEN_KEY] }
    val refreshToken: Flow<String?> = context.tokenDataStore.data.map { it[REFRESH_TOKEN_KEY] }

    val role: Flow<String?> = context.tokenDataStore.data.map { it[ROLE_KEY] }
    val userId: Flow<Int?> = context.tokenDataStore.data.map { it[USER_ID_KEY]?.toIntOrNull() }
    val email: Flow<String?> = context.tokenDataStore.data.map { it[EMAIL_KEY] }
    suspend fun saveToken(token: String, role: String, userId: Int, email: String) {
        saveTokens(token, "", role, userId, email)
    }

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        role: String,
        userId: Int,
        email: String
    ) {
        context.tokenDataStore.edit { prefs ->
            prefs[TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
            prefs[ROLE_KEY] = role
            prefs[USER_ID_KEY] = userId.toString()
            prefs[EMAIL_KEY] = email
        }
    }

    suspend fun clearTokens() {
        context.tokenDataStore.edit { it.clear() }
    }
    suspend fun getAccessToken(): String? = context.tokenDataStore.data.firstOrNull()?.get(TOKEN_KEY)
    suspend fun getRefreshToken(): String? = context.tokenDataStore.data.firstOrNull()?.get(REFRESH_TOKEN_KEY)
}