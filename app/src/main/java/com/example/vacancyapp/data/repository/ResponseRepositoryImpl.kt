package com.example.vacancyapp.data.repository

import com.example.vacancyapp.data.remote.ApiService
import com.example.vacancyapp.data.remote.dto.CreateResponseRequest
import com.example.vacancyapp.data.remote.dto.UpdateResponseStatusRequest
import com.example.vacancyapp.domain.models.VacancyResponse
import com.example.vacancyapp.domain.repository.ResponseRepository
import javax.inject.Inject

private fun com.example.vacancyapp.data.remote.dto.ResponseDto.toDomain() = VacancyResponse(
    id = id, userId = userId, vacancyId = vacancyId, resumeId = resumeId,
    status = status, coverLetter = coverLetter, createdAt = createdAt,
    updatedAt = updatedAt, vacancyTitle = vacancyTitle,
    companyName = companyName, applicantEmail = applicantEmail
)

class ResponseRepositoryImpl @Inject constructor(private val api: ApiService) : ResponseRepository {
    override suspend fun createResponse(token: String, vacancyId: Int, coverLetter: String?) =
        api.createResponse(token, CreateResponseRequest(vacancyId, coverLetter)).toDomain()
    override suspend fun getMyResponses(token: String) = api.getMyResponses(token).map { it.toDomain() }
    override suspend fun getVacancyResponses(token: String, vacancyId: Int) = api.getVacancyResponses(token, vacancyId).map { it.toDomain() }
    override suspend fun updateResponseStatus(token: String, id: Int, status: String) =
        api.updateResponseStatus(token, id, UpdateResponseStatusRequest(status)).toDomain()
    override suspend fun deleteResponse(token: String, id: Int) = api.deleteResponse(token, id)
}