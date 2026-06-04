package com.example.vacancyapp.data.remote

import com.example.vacancyapp.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiService @Inject constructor(private val client: HttpClient) {

    suspend fun login(request: LoginRequest): AuthResponse =
        client.post("/auth/login") { setBody(request) }.body()

    suspend fun register(request: RegisterRequest): AuthResponse =
        client.post("/auth/register") { setBody(request) }.body()

    suspend fun logout(token: String) {
        client.post("/auth/logout") { bearerAuth(token) }
    }

    suspend fun refreshToken(refreshToken: String): AuthResponse =
        client.post("/auth/refresh") {
            bearerAuth(refreshToken)
        }.body()

    suspend fun getVacancies(token: String): List<VacancyDto> =
        client.get("/vacancies") { bearerAuth(token) }.body()

    suspend fun searchVacancies(token: String, query: String): List<VacancyDto> =
        client.get("/vacancies/search") {
            bearerAuth(token)
            parameter("query", query)
        }.body()

    suspend fun getMyVacancies(token: String): List<VacancyDto> =
        client.get("/vacancies/my") { bearerAuth(token) }.body()

    suspend fun createVacancy(token: String, request: CreateVacancyRequest): VacancyDto =
        client.post("/vacancies") {
            bearerAuth(token)
            setBody(request)
        }.body()

    suspend fun updateVacancy(token: String, id: Int, request: UpdateVacancyRequest): VacancyDto =
        client.put("/vacancies/$id") {
            bearerAuth(token)
            setBody(request)
        }.body()

    suspend fun deleteVacancy(token: String, id: Int) {
        client.delete("/vacancies/$id") { bearerAuth(token) }
    }

    suspend fun getFavorites(token: String): List<VacancyDto> =
        client.get("/favorites") { bearerAuth(token) }.body()

    suspend fun addFavorite(token: String, id: Int) {
        client.post("/favorites/$id") { bearerAuth(token) }
    }

    suspend fun removeFavorite(token: String, id: Int) {
        client.delete("/favorites/$id") { bearerAuth(token) }
    }

    suspend fun getMyResume(token: String): ResumeDto =
        client.get("/resumes/my") { bearerAuth(token) }.body()

    suspend fun createResume(token: String, request: CreateResumeRequest): ResumeDto =
        client.post("/resumes") {
            bearerAuth(token)
            setBody(request)
        }.body()

    suspend fun updateResume(token: String, request: UpdateResumeRequest): ResumeDto =
        client.put("/resumes") {
            bearerAuth(token)
            setBody(request)
        }.body()

    suspend fun deleteResume(token: String) {
        client.delete("/resumes") { bearerAuth(token) }
    }

    suspend fun getResumeByUserId(token: String, userId: Int): ResumeDto =
        client.get("/resumes/user/$userId") { bearerAuth(token) }.body()

    suspend fun createResponse(token: String, request: CreateResponseRequest): ResponseDto =
        client.post("/responses") {
            bearerAuth(token)
            setBody(request)
        }.body()

    suspend fun getMyResponses(token: String): List<ResponseDto> =
        client.get("/responses/my") { bearerAuth(token) }.body()

    suspend fun getVacancyResponses(token: String, vacancyId: Int): List<ResponseDto> =
        client.get("/responses/vacancy/$vacancyId") { bearerAuth(token) }.body()

    suspend fun updateResponseStatus(token: String, id: Int, request: UpdateResponseStatusRequest): ResponseDto =
        client.put("/responses/$id/status") {
            bearerAuth(token)
            setBody(request)
        }.body()

    suspend fun deleteResponse(token: String, id: Int) {
        client.delete("/responses/$id") { bearerAuth(token) }
    }

    suspend fun getAdminUsers(token: String): List<UserDto> =
        client.get("/admin/users") { bearerAuth(token) }.body()

    suspend fun blockUser(token: String, id: Int) {
        client.put("/admin/users/$id/block") { bearerAuth(token) }
    }

    suspend fun unblockUser(token: String, id: Int) {
        client.put("/admin/users/$id/unblock") { bearerAuth(token) }
    }

    suspend fun changeRole(token: String, id: Int, request: ChangeRoleRequest) {
        client.put("/admin/users/$id/role") {
            bearerAuth(token)
            setBody(request)
        }
    }

    suspend fun getAdminStats(token: String): AdminStatsDto =
        client.get("/admin/stats") { bearerAuth(token) }.body()

    suspend fun adminDeleteVacancy(token: String, id: Int) {
        client.delete("/admin/vacancies/$id") { bearerAuth(token) }
    }

    private fun HttpRequestBuilder.bearerAuth(token: String) {
        headers { append(HttpHeaders.Authorization, "Bearer $token") }
    }
    suspend fun getMyProfile(token: String): ProfileDto =
        client.get("/profile") { bearerAuth(token) }.body()

    suspend fun updateProfile(token: String, request: UpdateProfileRequest): ProfileDto =
        client.put("/profile") {
            bearerAuth(token)
            setBody(request)
        }.body()
}