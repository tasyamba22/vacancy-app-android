package com.example.vacancyapp.domain.models

data class VacancyResponse(
    val id: Int,
    val userId: Int,
    val vacancyId: Int,
    val resumeId: Int,
    val status: String,
    val coverLetter: String?,
    val createdAt: String,
    val updatedAt: String,
    val vacancyTitle: String?,
    val companyName: String?,
    val applicantEmail: String?
)