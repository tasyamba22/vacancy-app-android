package com.example.vacancyapp.presentation.resume

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacancyapp.domain.models.Resume
import com.example.vacancyapp.domain.repository.ResumeRepository
import com.example.vacancyapp.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResumeUiState(
    val resume: Resume? = null,
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null,
    val fullName: String = "",
    val phone: String = "",
    val skills: String = "",
    val experience: String = "",
    val education: String = ""
)

@HiltViewModel
class ResumeViewModel @Inject constructor(
    private val resumeRepository: ResumeRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(ResumeUiState())
    val uiState = _uiState.asStateFlow()

    init { loadResume() }

    fun loadResume() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        try {
            val token = tokenManager.token.first() ?: return@launch
            val resume = resumeRepository.getMyResume(token)
            _uiState.update { it.copy(resume = resume, isLoading = false, fullName = resume.fullName, phone = resume.phone ?: "", skills = resume.skills ?: "", experience = resume.experience ?: "", education = resume.education ?: "") }
        } catch (_: Exception) {
            _uiState.update { it.copy(isLoading = false, resume = null) }
        }
    }

    fun onFullNameChange(v: String) = _uiState.update { it.copy(fullName = v) }
    fun onPhoneChange(v: String) = _uiState.update { it.copy(phone = v) }
    fun onSkillsChange(v: String) = _uiState.update { it.copy(skills = v) }
    fun onExperienceChange(v: String) = _uiState.update { it.copy(experience = v) }
    fun onEducationChange(v: String) = _uiState.update { it.copy(education = v) }

    fun save() = viewModelScope.launch {
        val state = _uiState.value
        if (state.fullName.isBlank()) { _uiState.update { it.copy(error = "ФИО обязательно") }; return@launch }
        _uiState.update { it.copy(isLoading = true) }
        try {
            val token = tokenManager.token.first() ?: return@launch
            if (state.resume == null) {
                resumeRepository.createResume(token, state.fullName, state.phone.ifBlank { null }, state.skills.ifBlank { null }, state.experience.ifBlank { null }, state.education.ifBlank { null })
            } else {
                resumeRepository.updateResume(token, state.fullName, state.phone.ifBlank { null }, state.skills.ifBlank { null }, state.experience.ifBlank { null }, state.education.ifBlank { null })
            }
            _uiState.update { it.copy(isSaved = true) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message, isLoading = false) }
        }
    }

    fun delete() = viewModelScope.launch {
        try {
            val token = tokenManager.token.first() ?: return@launch
            resumeRepository.deleteResume(token)
            _uiState.value = ResumeUiState(isLoading = false)
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }
}