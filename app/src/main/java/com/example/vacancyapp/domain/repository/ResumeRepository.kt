package com.example.vacancyapp.domain.repository

import com.example.vacancyapp.domain.models.Resume

interface ResumeRepository {
    suspend fun getMyResume(token: String): Resume
    suspend fun createResume(token: String, fullName: String, phone: String?, skills: String?, experience: String?, education: String?): Resume
    suspend fun updateResume(token: String, fullName: String?, phone: String?, skills: String?, experience: String?, education: String?): Resume
    suspend fun deleteResume(token: String)
    suspend fun getResumeByUserId(token: String, userId: Int): Resume
}