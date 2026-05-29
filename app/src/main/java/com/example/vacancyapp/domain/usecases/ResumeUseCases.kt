package com.example.vacancyapp.domain.usecases

import com.example.vacancyapp.domain.models.Resume
import com.example.vacancyapp.domain.repository.ResumeRepository
import javax.inject.Inject

class GetMyResumeUseCase @Inject constructor(private val repo: ResumeRepository) {
    suspend operator fun invoke(token: String): Resume = repo.getMyResume(token)
}

class GetResumeByUserIdUseCase @Inject constructor(private val repo: ResumeRepository) {
    suspend operator fun invoke(token: String, userId: Int): Resume = repo.getResumeByUserId(token, userId)
}

class CreateResumeUseCase @Inject constructor(private val repo: ResumeRepository) {
    suspend operator fun invoke(
        token: String, fullName: String, phone: String?, skills: String?, experience: String?, education: String?
    ): Resume {
        if (fullName.isBlank()) throw IllegalArgumentException("ФИО обязательно для заполнения")
        return repo.createResume(token, fullName.trim(), phone?.trim(), skills?.trim(), experience?.trim(), education?.trim())
    }
}

class UpdateResumeUseCase @Inject constructor(private val repo: ResumeRepository) {
    suspend operator fun invoke(
        token: String, fullName: String?, phone: String?, skills: String?, experience: String?, education: String?
    ): Resume {
        if (fullName != null && fullName.isBlank()) throw IllegalArgumentException("ФИО не может быть пустым")
        return repo.updateResume(token, fullName?.trim(), phone?.trim(), skills?.trim(), experience?.trim(), education?.trim())
    }
}

class DeleteResumeUseCase @Inject constructor(private val repo: ResumeRepository) {
    suspend operator fun invoke(token: String) = repo.deleteResume(token)
}