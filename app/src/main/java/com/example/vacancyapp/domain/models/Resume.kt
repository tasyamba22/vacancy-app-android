package com.example.vacancyapp.domain.models

data class Resume(
    val id: Int,
    val userId: Int,
    val fullName: String,
    val phone: String?,
    val skills: String?,
    val experience: String?,
    val education: String?,
    val createdAt: String,
    val updatedAt: String
)