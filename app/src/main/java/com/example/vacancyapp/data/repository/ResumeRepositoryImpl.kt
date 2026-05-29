package com.example.vacancyapp.data.repository

import com.example.vacancyapp.data.remote.ApiService
import com.example.vacancyapp.data.remote.dto.CreateResumeRequest
import com.example.vacancyapp.data.remote.dto.UpdateResumeRequest
import com.example.vacancyapp.domain.models.Resume
import com.example.vacancyapp.domain.repository.ResumeRepository
import javax.inject.Inject

private fun com.example.vacancyapp.data.remote.dto.ResumeDto.toDomain() = Resume(
    id = id, userId = userId, fullName = fullName, phone = phone,
    skills = skills, experience = experience, education = education,
    createdAt = createdAt, updatedAt = updatedAt
)

class ResumeRepositoryImpl @Inject constructor(private val api: ApiService) : ResumeRepository {
    override suspend fun getMyResume(token: String) = api.getMyResume(token).toDomain()
    override suspend fun createResume(token: String, fullName: String, phone: String?, skills: String?, experience: String?, education: String?) =
        api.createResume(token, CreateResumeRequest(fullName, phone, skills, experience, education)).toDomain()
    override suspend fun updateResume(token: String, fullName: String?, phone: String?, skills: String?, experience: String?, education: String?) =
        api.updateResume(token, UpdateResumeRequest(fullName, phone, skills, experience, education)).toDomain()
    override suspend fun deleteResume(token: String) = api.deleteResume(token)
    override suspend fun getResumeByUserId(token: String, userId: Int) = api.getResumeByUserId(token, userId).toDomain()
}