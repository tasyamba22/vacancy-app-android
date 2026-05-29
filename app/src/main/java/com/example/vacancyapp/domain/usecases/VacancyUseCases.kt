package com.example.vacancyapp.domain.usecases

import com.example.vacancyapp.domain.models.Vacancy
import com.example.vacancyapp.domain.repository.VacancyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVacanciesUseCase @Inject constructor(private val repo: VacancyRepository) {
    operator fun invoke(): Flow<List<Vacancy>> = repo.getVacancies()
}

class SearchVacanciesUseCase @Inject constructor(private val repo: VacancyRepository) {
    operator fun invoke(query: String): Flow<List<Vacancy>> {
        if (query.isBlank()) return repo.getVacancies()
        return repo.searchVacancies(query.trim())
    }
}

class GetMyVacanciesUseCase @Inject constructor(private val repo: VacancyRepository) {
    operator fun invoke(userId: Int): Flow<List<Vacancy>> = repo.getMyVacancies(userId)
}

class GetFavoritesUseCase @Inject constructor(private val repo: VacancyRepository) {
    operator fun invoke(): Flow<List<Vacancy>> = repo.getFavorites()
}

class CreateVacancyUseCase @Inject constructor(private val repo: VacancyRepository) {
    suspend operator fun invoke(
        token: String, title: String, company: String,
        salary: String?, location: String?, description: String?
    ): Vacancy {
        if (title.isBlank()) throw IllegalArgumentException("Название вакансии обязательно")
        if (company.isBlank()) throw IllegalArgumentException("Название компании обязательно")
        return repo.createVacancy(token, title.trim(), company.trim(), salary?.trim(), location?.trim(), description?.trim())
    }
}

class UpdateVacancyUseCase @Inject constructor(private val repo: VacancyRepository) {
    suspend operator fun invoke(
        token: String, id: Int,
        title: String?, company: String?, salary: String?, location: String?, description: String?
    ): Vacancy {
        if (title != null && title.isBlank()) throw IllegalArgumentException("Название не может быть пустым")
        if (company != null && company.isBlank()) throw IllegalArgumentException("Компания не может быть пустой")
        return repo.updateVacancy(token, id, title?.trim(), company?.trim(), salary?.trim(), location?.trim(), description?.trim())
    }
}

class DeleteVacancyUseCase @Inject constructor(private val repo: VacancyRepository) {
    suspend operator fun invoke(token: String, id: Int) = repo.deleteVacancy(token, id)
}

class SyncVacanciesUseCase @Inject constructor(private val repo: VacancyRepository) {
    suspend operator fun invoke(token: String) {
        repo.syncVacancies(token)
        repo.syncFavorites(token)
    }
}

class AddFavoriteUseCase @Inject constructor(private val repo: VacancyRepository) {
    suspend operator fun invoke(token: String, id: Int) = repo.addFavorite(token, id)
}

class RemoveFavoriteUseCase @Inject constructor(private val repo: VacancyRepository) {
    suspend operator fun invoke(token: String, id: Int) = repo.removeFavorite(token, id)
}