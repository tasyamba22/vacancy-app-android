package com.example.vacancyapp.presentation.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacancyapp.R
import com.example.vacancyapp.data.remote.ApiService
import com.example.vacancyapp.data.remote.dto.UpdateProfileRequest
import com.example.vacancyapp.domain.repository.AuthRepository
import com.example.vacancyapp.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val role: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val isOffline: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository,
    private val apiService: ApiService,
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init { loadProfile() }

    private fun loadProfile() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null, isOffline = false) }
        try {
            val token = tokenManager.token.first() ?: return@launch
            val email = tokenManager.email.first() ?: ""
            val role = tokenManager.role.first() ?: ""

            try {
                val profile = apiService.getMyProfile(token)
                _uiState.update {
                    it.copy(
                        email = profile.email,
                        firstName = profile.firstName ?: "",
                        lastName = profile.lastName ?: "",
                        role = profile.role,
                        isLoading = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        email = email,
                        role = role,
                        isLoading = false,
                        isOffline = true,
                        error = getApplication<Application>().getString(R.string.profile_offline_message)
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message, isLoading = false) }
        }
    }

    fun saveProfile() = viewModelScope.launch {
        val currentFirstName = _uiState.value.firstName
        val currentLastName = _uiState.value.lastName

        _uiState.update { it.copy(isLoading = true, isSaved = false, error = null) }

        try {
            val token = tokenManager.token.first() ?: return@launch
            apiService.updateProfile(
                token = token,
                request = UpdateProfileRequest(
                    firstName = currentFirstName.ifBlank { null },
                    lastName = currentLastName.ifBlank { null }
                )
            )
            _uiState.update {
                it.copy(
                    firstName = currentFirstName,
                    lastName = currentLastName,
                    isLoading = false,
                    isSaved = true,
                    error = null
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    error = getApplication<Application>().getString(
                        R.string.profile_error_save, e.message ?: ""
                    ),
                    isLoading = false
                )
            }
        }
    }

    fun onFirstNameChange(v: String) = _uiState.update { it.copy(firstName = v) }
    fun onLastNameChange(v: String) = _uiState.update { it.copy(lastName = v) }

    fun logout(onDone: () -> Unit) = viewModelScope.launch {
        try {
            val token = tokenManager.token.first()
            if (token != null) authRepository.logout(token)
        } catch (_: Exception) {}
        tokenManager.clearTokens()
        onDone()
    }
}