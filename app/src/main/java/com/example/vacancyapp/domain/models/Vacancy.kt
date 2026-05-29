package com.example.vacancyapp.domain.models

data class Vacancy(
    val id: Int,
    val userId: Int,
    val title: String,
    val company: String,
    val salary: String?,
    val location: String?,
    val description: String?,
    val createdAt: String,
    val isFavorite: Boolean = false
)