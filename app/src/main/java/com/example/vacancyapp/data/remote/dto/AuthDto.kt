package com.example.vacancyapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(val email: String, val password: String)

@Serializable
data class AuthResponse(val token: String, val role: String, val userId: Int)