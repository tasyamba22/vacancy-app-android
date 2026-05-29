package com.example.vacancyapp.data.repository

import com.example.vacancyapp.data.remote.ApiService
import com.example.vacancyapp.data.remote.dto.ChangeRoleRequest
import com.example.vacancyapp.domain.models.AdminStats
import com.example.vacancyapp.domain.models.User
import com.example.vacancyapp.domain.repository.AdminRepository
import javax.inject.Inject

private fun com.example.vacancyapp.data.remote.dto.UserDto.toDomain() =
    User(id = id, email = email, role = role, isBlocked = isBlocked, createdAt = createdAt)

class AdminRepositoryImpl @Inject constructor(private val api: ApiService) : AdminRepository {
    override suspend fun getUsers(token: String) = api.getAdminUsers(token).map { it.toDomain() }
    override suspend fun blockUser(token: String, id: Int) = api.blockUser(token, id)
    override suspend fun unblockUser(token: String, id: Int) = api.unblockUser(token, id)
    override suspend fun changeRole(token: String, id: Int, role: String) = api.changeRole(token, id, ChangeRoleRequest(role))
    override suspend fun getStats(token: String) = api.getAdminStats(token).let { AdminStats(it.totalUsers, it.totalVacancies) }
    override suspend fun deleteVacancy(token: String, id: Int) = api.adminDeleteVacancy(token, id)
}