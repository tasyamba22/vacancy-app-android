package com.example.vacancyapp.data.repository

import com.example.vacancyapp.data.remote.ApiService
import com.example.vacancyapp.data.remote.dto.LoginRequest
import com.example.vacancyapp.data.remote.dto.RegisterRequest
import com.example.vacancyapp.domain.models.AuthResult
import com.example.vacancyapp.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService
) : AuthRepository {
    override suspend fun login(email: String, password: String): AuthResult {
        val response = api.login(LoginRequest(email, password))
        return AuthResult(response.token, refreshToken = response.refreshToken, response.role, response.userId)
    }

    override suspend fun register(email: String, password: String): AuthResult {
        val response = api.register(RegisterRequest(email, password))
        return AuthResult(response.token, refreshToken = response.refreshToken, response.role, response.userId)
    }

    override suspend fun logout(token: String) = api.logout(token)
}