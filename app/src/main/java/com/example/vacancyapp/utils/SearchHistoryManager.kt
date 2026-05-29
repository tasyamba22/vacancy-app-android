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

private val Context.searchDataStore by preferencesDataStore("search_prefs")

@Singleton
class SearchHistoryManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val HISTORY_KEY = stringPreferencesKey("search_history")
        private const val MAX_HISTORY = 10
        private const val SEPARATOR = "|||"
    }

    val searchHistory: Flow<List<String>> = context.searchDataStore.data.map { prefs ->
        prefs[HISTORY_KEY]?.split(SEPARATOR)?.filter { it.isNotBlank() } ?: emptyList()
    }

    suspend fun addQuery(query: String) {
        if (query.isBlank()) return
        context.searchDataStore.edit { prefs ->
            val current = prefs[HISTORY_KEY]
                ?.split(SEPARATOR)
                ?.filter { it.isNotBlank() && it != query }
                ?.toMutableList() ?: mutableListOf()
            current.add(0, query)
            prefs[HISTORY_KEY] = current.take(MAX_HISTORY).joinToString(SEPARATOR)
        }
    }

    suspend fun clearHistory() {
        context.searchDataStore.edit { it.remove(HISTORY_KEY) }
    }
}