package com.example.vacancyapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vacancies")
data class VacancyEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val company: String,
    val salary: String?,
    val location: String?,
    val description: String?,
    val createdAt: String,
    val isFavorite: Boolean = false
)