package com.example.vacancyapp.domain.models

data class User(
    val id: Int,
    val email: String,
    val role: String,
    val isBlocked: Boolean,
    val createdAt: String
)