package com.example.vacancyapp.domain.repository

import com.example.vacancyapp.domain.models.AuthResult

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String): AuthResult
    suspend fun logout(token: String)
}