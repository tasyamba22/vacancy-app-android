package com.example.vacancyapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val result = authRepository.login(email, password)
                tokenManager.saveToken(
                    token = result.token,
                    role = result.role,
                    userId = result.userId,
                    email = email
                )
                _uiState.value = AuthUiState(isSuccess = true)
            } catch (e: io.ktor.client.plugins.ClientRequestException) {
                val errorMessage = when (e.response.status.value) {
                    401 -> "Неверный email или пароль"
                    403 -> "Ваш аккаунт заблокирован"
                    else -> "Ошибка входа"
                }
                _uiState.value = AuthUiState(error = errorMessage)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = "Неверный email или пароль")
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val result = authRepository.register(email, password)
                tokenManager.saveToken(
                    token = result.token,
                    role = result.role,
                    userId = result.userId,
                    email = email
                )
                _uiState.value = AuthUiState(isSuccess = true)
            } catch (e: ClientRequestException) {
                val errorMessage = when (e.response.status.value) {
                    409 -> "Пользователь с таким email уже существует"
                    400 -> "Пароль должен быть не менее 6 символов"
                    else -> "Ошибка регистрации"
                }
                _uiState.value = AuthUiState(error = errorMessage)
            } catch (e: IllegalArgumentException) {
                _uiState.value = AuthUiState(error = e.message ?: "Ошибка")
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = "Ошибка регистрации")
            }
        }
    }
}