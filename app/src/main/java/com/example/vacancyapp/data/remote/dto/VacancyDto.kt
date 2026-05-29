package com.example.vacancyapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VacancyDto(
    val id: Int,
    val userId: Int,
    val title: String,
    val company: String,
    val salary: String? = null,
    val location: String? = null,
    val description: String? = null,
    val createdAt: String,
    val isFavorite: Boolean = false
)

@Serializable
data class CreateVacancyRequest(
    val title: String,
    val company: String,
    val salary: String? = null,
    val location: String? = null,
    val description: String? = null
)

@Serializable
data class UpdateVacancyRequest(
    val title: String? = null,
    val company: String? = null,
    val salary: String? = null,
    val location: String? = null,
    val description: String? = null
)