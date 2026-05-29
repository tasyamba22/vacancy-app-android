package com.example.vacancyapp.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacancyapp.domain.models.Resume
import com.example.vacancyapp.domain.models.Vacancy
import com.example.vacancyapp.domain.models.VacancyResponse
import com.example.vacancyapp.domain.repository.ResumeRepository
import com.example.vacancyapp.domain.repository.ResponseRepository
import com.example.vacancyapp.domain.repository.VacancyRepository
import com.example.vacancyapp.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val vacancy: Vacancy? = null,
    val myResume: Resume? = null,
    val myResponse: VacancyResponse? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isOwner: Boolean = false,
    val responseSuccess: Boolean = false
)

@HiltViewModel
class VacancyDetailViewModel @Inject constructor(
    private val vacancyRepository: VacancyRepository,
    private val resumeRepository: ResumeRepository,
    private val responseRepository: ResponseRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    fun load(vacancyId: Int) = viewModelScope.launch {
        _uiState.value = DetailUiState(isLoading = true)
        try {
            val token = tokenManager.token.first() ?: return@launch
            val userId = tokenManager.userId.first()

            val vacancy = vacancyRepository.getVacancies().first().find { it.id == vacancyId }
            val isOwner = vacancy?.userId == userId

            var myResume: Resume? = null
            var myResponse: VacancyResponse? = null

            if (!isOwner) {
                try { myResume = resumeRepository.getMyResume(token) } catch (_: Exception) {}
                try {
                    val responses = responseRepository.getMyResponses(token)
                    myResponse = responses.find { it.vacancyId == vacancyId }
                } catch (_: Exception) {}
            }

            _uiState.value = DetailUiState(
                vacancy = vacancy,
                myResume = myResume,
                myResponse = myResponse,
                isLoading = false,
                isOwner = isOwner
            )
        } catch (e: Exception) {
            _uiState.value = DetailUiState(isLoading = false, error = e.message)
        }
    }

    fun respond(vacancyId: Int, coverLetter: String?) = viewModelScope.launch {
        try {
            val token = tokenManager.token.first() ?: return@launch
            val response = responseRepository.createResponse(token, vacancyId, coverLetter)
            _uiState.update { it.copy(myResponse = response, responseSuccess = true) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }

    fun deleteVacancy(id: Int, onDone: () -> Unit) = viewModelScope.launch {
        try {
            val token = tokenManager.token.first() ?: return@launch
            vacancyRepository.deleteVacancy(token, id)
            onDone()
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }
}