package com.example.vacancyapp.domain.models

data class AuthResult(val token: String, val role: String, val userId: Int)
