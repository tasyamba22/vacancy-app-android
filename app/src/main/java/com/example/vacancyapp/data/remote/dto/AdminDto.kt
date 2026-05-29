package com.example.vacancyapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    val role: String,
    val isBlocked: Boolean,
    val createdAt: String
)

@Serializable
data class AdminStatsDto(
    val totalUsers: Long,
    val totalVacancies: Long
)

@Serializable
data class ChangeRoleRequest(val role: String)