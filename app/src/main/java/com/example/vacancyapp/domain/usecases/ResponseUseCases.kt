package com.example.vacancyapp.domain.usecases

import com.example.vacancyapp.domain.models.VacancyResponse
import com.example.vacancyapp.domain.repository.ResponseRepository
import com.example.vacancyapp.utils.ResponseStatus
import javax.inject.Inject

class CreateResponseUseCase @Inject constructor(private val repo: ResponseRepository) {
    suspend operator fun invoke(token: String, vacancyId: Int, coverLetter: String?): VacancyResponse {
        if (vacancyId <= 0) throw IllegalArgumentException("Неверный ID вакансии")
        return repo.createResponse(token, vacancyId, coverLetter?.trim())
    }
}

class GetMyResponsesUseCase @Inject constructor(private val repo: ResponseRepository) {
    suspend operator fun invoke(token: String): List<VacancyResponse> = repo.getMyResponses(token)
}

class GetVacancyResponsesUseCase @Inject constructor(private val repo: ResponseRepository) {
    suspend operator fun invoke(token: String, vacancyId: Int): List<VacancyResponse> = repo.getVacancyResponses(token, vacancyId)
}

class UpdateResponseStatusUseCase @Inject constructor(private val repo: ResponseRepository) {
    suspend operator fun invoke(token: String, id: Int, status: String): VacancyResponse {
        val valid = listOf(ResponseStatus.PENDING, ResponseStatus.ACCEPTED, ResponseStatus.REJECTED)
        if (status !in valid) throw IllegalArgumentException("Неверный статус")
        return repo.updateResponseStatus(token, id, status)
    }
}

class DeleteResponseUseCase @Inject constructor(private val repo: ResponseRepository) {
    suspend operator fun invoke(token: String, id: Int) = repo.deleteResponse(token, id)
}