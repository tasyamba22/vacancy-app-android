package com.example.vacancyapp.domain.repository

import com.example.vacancyapp.domain.models.Vacancy
import kotlinx.coroutines.flow.Flow

interface VacancyRepository {
    fun getVacancies(): Flow<List<Vacancy>>
    fun searchVacancies(query: String): Flow<List<Vacancy>>
    fun getMyVacancies(userId: Int): Flow<List<Vacancy>>
    fun getFavorites(): Flow<List<Vacancy>>
    fun searchVacanciesWithFilter(
        query: String,
        city: String,
        company: String,
        salaryFrom: String,
        salaryTo: String
    ): Flow<List<Vacancy>>
    suspend fun getMyVacancies(token: String): List<Vacancy>
    suspend fun syncVacancies(token: String)
    suspend fun createVacancy(token: String, title: String, company: String, salary: String?, location: String?, description: String?): Vacancy
    suspend fun updateVacancy(token: String, id: Int, title: String?, company: String?, salary: String?, location: String?, description: String?): Vacancy
    suspend fun deleteVacancy(token: String, id: Int)
    suspend fun addFavorite(token: String, id: Int)
    suspend fun removeFavorite(token: String, id: Int)
    suspend fun syncFavorites(token: String)
}