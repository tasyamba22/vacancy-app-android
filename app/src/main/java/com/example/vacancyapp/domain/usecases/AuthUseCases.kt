package com.example.vacancyapp.domain.usecases

import com.example.vacancyapp.domain.models.AuthResult
import com.example.vacancyapp.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        if (email.isBlank()) throw IllegalArgumentException("Email не может быть пустым")
        if (password.isBlank()) throw IllegalArgumentException("Пароль не может быть пустым")
        if (!email.contains("@")) throw IllegalArgumentException("Неверный формат email")
        return repository.login(email.trim(), password)
    }
}

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        if (email.isBlank()) throw IllegalArgumentException("Email не может быть пустым")
        if (password.isBlank()) throw IllegalArgumentException("Пароль не может быть пустым")
        if (!email.contains("@")) throw IllegalArgumentException("Неверный формат email")
        if (password.length < 6) throw IllegalArgumentException("Пароль должен быть не менее 6 символов")
        return repository.register(email.trim(), password)
    }
}

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(token: String) {
        repository.logout(token)
    }
}