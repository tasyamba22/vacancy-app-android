package com.example.vacancyapp.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacancyapp.domain.models.AdminStats
import com.example.vacancyapp.domain.models.User
import com.example.vacancyapp.domain.repository.AdminRepository
import com.example.vacancyapp.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val users: List<User> = emptyList(),
    val stats: AdminStats? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun load() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val token = tokenManager.token.first() ?: return@launch
            val users = adminRepository.getUsers(token)
            val stats = adminRepository.getStats(token)
            _uiState.update { it.copy(users = users, stats = stats, isLoading = false) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message, isLoading = false) }
        }
    }

    fun blockUser(id: Int) = viewModelScope.launch {
        try {
            val token = tokenManager.token.first() ?: return@launch
            adminRepository.blockUser(token, id)
            _uiState.update { s -> s.copy(users = s.users.map { if (it.id == id) it.copy(isBlocked = true) else it }) }
        } catch (_: Exception) {}
    }

    fun unblockUser(id: Int) = viewModelScope.launch {
        try {
            val token = tokenManager.token.first() ?: return@launch
            adminRepository.unblockUser(token, id)
            _uiState.update { s -> s.copy(users = s.users.map { if (it.id == id) it.copy(isBlocked = false) else it }) }
        } catch (_: Exception) {}
    }

    fun changeRole(id: Int, role: String) = viewModelScope.launch {
        try {
            val token = tokenManager.token.first() ?: return@launch
            adminRepository.changeRole(token, id, role)
            _uiState.update { s -> s.copy(users = s.users.map { if (it.id == id) it.copy(role = role) else it }) }
        } catch (_: Exception) {}
    }
}