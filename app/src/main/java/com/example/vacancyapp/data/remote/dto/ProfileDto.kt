package com.example.vacancyapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: Int,
    val email: String,
    val role: String,
    val firstName: String?,
    val lastName: String?
)

@Serializable
data class UpdateProfileRequest(
    val firstName: String?,
    val lastName: String?
)