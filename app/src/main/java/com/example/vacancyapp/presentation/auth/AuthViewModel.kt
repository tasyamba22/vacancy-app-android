package com.example.vacancyapp.presentation.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacancyapp.R
import com.example.vacancyapp.domain.repository.AuthRepository
import com.example.vacancyapp.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val result = authRepository.login(email, password)
                tokenManager.saveTokens(
                    accessToken = result.token,
                    refreshToken = result.refreshToken,
                    role = result.role,
                    userId = result.userId,
                    email = email
                )
                _uiState.value = AuthUiState(isSuccess = true)
            } catch (e: ClientRequestException) {
                val ctx = getApplication<Application>()
                val errorMessage = when (e.response.status.value) {
                    401 -> ctx.getString(R.string.login_error_wrong_credentials)
                    403 -> ctx.getString(R.string.login_error_blocked)
                    else -> ctx.getString(R.string.login_error_generic)
                }
                _uiState.value = AuthUiState(error = errorMessage)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    error = getApplication<Application>().getString(R.string.login_error_wrong_credentials)
                )
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val result = authRepository.register(email, password)
                tokenManager.saveTokens(
                    accessToken = result.token,
                    refreshToken = result.refreshToken,
                    role = result.role,
                    userId = result.userId,
                    email = email
                )
                _uiState.value = AuthUiState(isSuccess = true)
            } catch (e: ClientRequestException) {
                val ctx = getApplication<Application>()
                val errorMessage = when (e.response.status.value) {
                    409 -> ctx.getString(R.string.register_error_email_exists)
                    400 -> ctx.getString(R.string.register_error_password_short)
                    else -> ctx.getString(R.string.register_error_generic)
                }
                _uiState.value = AuthUiState(error = errorMessage)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    error = getApplication<Application>().getString(R.string.register_error_generic)
                )
            }
        }
    }
}