package com.example.vacancyapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResponseDto(
    val id: Int,
    val userId: Int,
    val vacancyId: Int,
    val resumeId: Int,
    val status: String,
    val coverLetter: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val vacancyTitle: String? = null,
    val companyName: String? = null,
    val applicantEmail: String? = null
)

@Serializable
data class CreateResponseRequest(
    val vacancyId: Int,
    val coverLetter: String? = null
)

@Serializable
data class UpdateResponseStatusRequest(val status: String)