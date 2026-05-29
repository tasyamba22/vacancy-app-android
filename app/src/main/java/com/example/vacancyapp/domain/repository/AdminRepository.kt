package com.example.vacancyapp.domain.repository

import com.example.vacancyapp.domain.models.AdminStats
import com.example.vacancyapp.domain.models.User

interface AdminRepository {
    suspend fun getUsers(token: String): List<User>
    suspend fun blockUser(token: String, id: Int)
    suspend fun unblockUser(token: String, id: Int)
    suspend fun changeRole(token: String, id: Int, role: String)
    suspend fun getStats(token: String): AdminStats
    suspend fun deleteVacancy(token: String, id: Int)
}