package com.example.vacancyapp.domain.repository

import com.example.vacancyapp.domain.models.VacancyResponse

interface ResponseRepository {
    suspend fun createResponse(token: String, vacancyId: Int, coverLetter: String?): VacancyResponse
    suspend fun getMyResponses(token: String): List<VacancyResponse>
    suspend fun getVacancyResponses(token: String, vacancyId: Int): List<VacancyResponse>
    suspend fun updateResponseStatus(token: String, id: Int, status: String): VacancyResponse
    suspend fun deleteResponse(token: String, id: Int)
}