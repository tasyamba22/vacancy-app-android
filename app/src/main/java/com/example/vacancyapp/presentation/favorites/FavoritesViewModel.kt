package com.example.vacancyapp.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacancyapp.domain.models.Vacancy
import com.example.vacancyapp.domain.repository.VacancyRepository
import com.example.vacancyapp.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val vacancyRepository: VacancyRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    val favorites: StateFlow<List<Vacancy>> = vacancyRepository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeFavorite(id: Int) = viewModelScope.launch {
        val token = tokenManager.token.first() ?: return@launch
        vacancyRepository.removeFavorite(token, id)
    }
}