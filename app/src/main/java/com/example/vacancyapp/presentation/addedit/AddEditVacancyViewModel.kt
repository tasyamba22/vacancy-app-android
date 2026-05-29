package com.example.vacancyapp.presentation.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacancyapp.domain.repository.VacancyRepository
import com.example.vacancyapp.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditUiState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val title: String = "",
    val company: String = "",
    val salary: String = "",
    val location: String = "",
    val description: String = ""
)

@HiltViewModel
class AddEditVacancyViewModel @Inject constructor(
    private val vacancyRepository: VacancyRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState = _uiState.asStateFlow()

    fun loadVacancy(id: Int) = viewModelScope.launch {
        val vacancy = vacancyRepository.getVacancies().first().find { it.id == id } ?: return@launch
        _uiState.update { it.copy(title = vacancy.title, company = vacancy.company, salary = vacancy.salary ?: "", location = vacancy.location ?: "", description = vacancy.description ?: "") }
    }

    fun onTitleChange(v: String) = _uiState.update { it.copy(title = v) }
    fun onCompanyChange(v: String) = _uiState.update { it.copy(company = v) }
    fun onSalaryChange(v: String) = _uiState.update { it.copy(salary = v) }
    fun onLocationChange(v: String) = _uiState.update { it.copy(location = v) }
    fun onDescriptionChange(v: String) = _uiState.update { it.copy(description = v) }

    fun save(existingId: Int?) = viewModelScope.launch {
        val state = _uiState.value
        if (state.title.isBlank() || state.company.isBlank()) {
            _uiState.update { it.copy(error = "Название и компания обязательны") }
            return@launch
        }
        _uiState.update { it.copy(isLoading = true) }
        try {
            val token = tokenManager.token.first() ?: return@launch
            if (existingId != null) {
                vacancyRepository.updateVacancy(token, existingId, state.title, state.company, state.salary.ifBlank { null }, state.location.ifBlank { null }, state.description.ifBlank { null })
            } else {
                vacancyRepository.createVacancy(token, state.title, state.company, state.salary.ifBlank { null }, state.location.ifBlank { null }, state.description.ifBlank { null })
            }
            _uiState.update { it.copy(isSaved = true) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message, isLoading = false) }
        }
    }
}