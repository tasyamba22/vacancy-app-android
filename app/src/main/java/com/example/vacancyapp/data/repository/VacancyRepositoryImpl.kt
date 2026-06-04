package com.example.vacancyapp.data.repository

import android.util.Log
import com.example.vacancyapp.data.local.VacancyDao
import com.example.vacancyapp.data.remote.ApiService
import com.example.vacancyapp.data.remote.dto.CreateVacancyRequest
import com.example.vacancyapp.data.remote.dto.UpdateVacancyRequest
import com.example.vacancyapp.domain.models.Vacancy
import com.example.vacancyapp.domain.repository.VacancyRepository
import com.example.vacancyapp.utils.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import io.ktor.client.plugins.ClientRequestException

private fun com.example.vacancyapp.data.local.VacancyEntity.toDomain() =
    com.example.vacancyapp.domain.models.Vacancy(
        id = id,
        userId = userId,
        title = title,
        company = company,
        salary = salary,
        location = location,
        description = description,
        createdAt = createdAt,
        isFavorite = isFavorite
    )

private fun com.example.vacancyapp.data.remote.dto.VacancyDto.toEntity() =
    com.example.vacancyapp.data.local.VacancyEntity(
        id = id,
        userId = userId,
        title = title,
        company = company,
        salary = salary,
        location = location,
        description = description,
        createdAt = createdAt,
        isFavorite = isFavorite
    )

private fun com.example.vacancyapp.data.remote.dto.VacancyDto.toDomain() =
    com.example.vacancyapp.domain.models.Vacancy(
        id = id,
        userId = userId,
        title = title,
        company = company,
        salary = salary,
        location = location,
        description = description,
        createdAt = createdAt,
        isFavorite = isFavorite
    )

class VacancyRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val dao: VacancyDao,
    private val tokenManager: TokenManager
) : VacancyRepository {

    override fun getVacancies(): Flow<List<Vacancy>> =
        dao.getAllVacancies().map { list -> list.map { it.toDomain() } }

    override fun searchVacancies(query: String): Flow<List<Vacancy>> =
        dao.searchVacancies(query).map { list -> list.map { it.toDomain() } }

    override fun getMyVacancies(userId: Int): Flow<List<Vacancy>> =
        dao.getMyVacancies(userId).map { list -> list.map { it.toDomain() } }

    override fun getFavorites(): Flow<List<Vacancy>> =
        dao.getFavorites().map { list -> list.map { it.toDomain() } }

    override suspend fun syncVacancies(token: String) {
        try {
            val remote = api.getVacancies(token)
            dao.deleteAll()
            dao.upsertAll(remote.map { it.toEntity() })
            Log.d("VacancyRepo", "Успешно загружено ${remote.size} вакансий с сервера")
        } catch (e: ClientRequestException) {
            if (e.response.status.value == 401) {
                val refreshToken = tokenManager.getRefreshToken()
                if (refreshToken != null) {
                    try {
                        val newAuth = api.refreshToken(refreshToken)
                        tokenManager.saveTokens(
                            accessToken = newAuth.token,
                            refreshToken = newAuth.refreshToken,
                            role = newAuth.role,
                            userId = newAuth.userId,
                            email = ""
                        )
                        val newToken = newAuth.token
                        val remote = api.getVacancies(newToken)
                        dao.deleteAll()
                        dao.upsertAll(remote.map { it.toEntity() })
                        return
                    } catch (refreshEx: Exception) {
                        Log.e("VacancyRepo", "Refresh failed")
                        tokenManager.clearTokens()
                        throw Exception("Сессия истекла")
                    }
                }
            }
            Log.e("VacancyRepo", "Sync failed, using cache: ${e.message}")
        } catch (e: Exception) {
            Log.e("VacancyRepo", "Sync failed, using cache: ${e.message}")
        }
    }

    override suspend fun syncFavorites(token: String) {
        val favorites = api.getFavorites(token)
        favorites.forEach { dto ->
            val entity = dao.getById(dto.id)
            if (entity != null) {
                dao.upsert(entity.copy(isFavorite = true))
            }
        }
    }

    override suspend fun createVacancy(
        token: String,
        title: String,
        company: String,
        salary: String?,
        location: String?,
        description: String?
    ): Vacancy {
        val dto = api.createVacancy(
            token,
            CreateVacancyRequest(title, company, salary, location, description)
        )
        dao.upsert(dto.toEntity())
        return dto.toDomain()
    }

    override suspend fun updateVacancy(
        token: String,
        id: Int,
        title: String?,
        company: String?,
        salary: String?,
        location: String?,
        description: String?
    ): Vacancy {
        val dto = api.updateVacancy(
            token,
            id,
            UpdateVacancyRequest(title, company, salary, location, description)
        )
        dao.upsert(dto.toEntity())
        return dto.toDomain()
    }

    override suspend fun deleteVacancy(token: String, id: Int) {
        api.deleteVacancy(token, id)
        dao.deleteById(id)
    }

    override suspend fun addFavorite(token: String, id: Int) {
        api.addFavorite(token, id)
        val entity = dao.getById(id)
        entity?.let { dao.upsert(it.copy(isFavorite = true)) }
    }

    override suspend fun removeFavorite(token: String, id: Int) {
        api.removeFavorite(token, id)
        val entity = dao.getById(id)
        entity?.let { dao.upsert(it.copy(isFavorite = false)) }
    }

    override suspend fun getMyVacancies(token: String): List<Vacancy> {
        val dtos = api.getMyVacancies(token)
        return dtos.map { it.toDomain() }
    }

    override fun searchVacanciesWithFilter(
        query: String,
        city: String,
        company: String,
        salaryFrom: String,
        salaryTo: String
    ): Flow<List<Vacancy>> =
        dao.searchVacanciesWithFilter(query, city, company, salaryFrom, salaryTo)
            .map { list -> list.map { it.toDomain() } }
}