package com.example.vacancyapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResumeDto(
    val id: Int,
    val userId: Int,
    val fullName: String,
    val phone: String? = null,
    val skills: String? = null,
    val experience: String? = null,
    val education: String? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateResumeRequest(
    val fullName: String,
    val phone: String? = null,
    val skills: String? = null,
    val experience: String? = null,
    val education: String? = null
)

@Serializable
data class UpdateResumeRequest(
    val fullName: String? = null,
    val phone: String? = null,
    val skills: String? = null,
    val experience: String? = null,
    val education: String? = null
)