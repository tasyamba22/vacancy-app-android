package com.example.vacancyapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VacancyDao {
    @Query("SELECT * FROM vacancies ORDER BY createdAt DESC")
    fun getAllVacancies(): Flow<List<VacancyEntity>>

    @Query("SELECT * FROM vacancies WHERE title LIKE '%' || :query || '%' OR company LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchVacancies(query: String): Flow<List<VacancyEntity>>

    @Query("SELECT * FROM vacancies WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<VacancyEntity>>

    @Query("SELECT * FROM vacancies WHERE userId = :userId")
    fun getMyVacancies(userId: Int): Flow<List<VacancyEntity>>

    @Query("SELECT * FROM vacancies WHERE id = :id")
    suspend fun getById(id: Int): VacancyEntity?

    @Upsert
    suspend fun upsertAll(vacancies: List<VacancyEntity>)

    @Upsert
    suspend fun upsert(vacancy: VacancyEntity)

    @Query("DELETE FROM vacancies WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM vacancies")
    suspend fun deleteAll()

    @Query("""
    SELECT * FROM vacancies 
    WHERE (
        title LIKE '%' || :query || '%' 
        OR company LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%'
    )
    AND (:city = '' OR location LIKE '%' || :city || '%')
    AND (:company = '' OR company LIKE '%' || :company || '%')
    AND (:salaryFrom = '' OR salary >= :salaryFrom)
    AND (:salaryTo = '' OR salary <= :salaryTo)
    ORDER BY createdAt DESC
    """)
    fun searchVacanciesWithFilter(
        query: String,
        city: String,
        company: String,
        salaryFrom: String,
        salaryTo: String
    ): Flow<List<VacancyEntity>>
}