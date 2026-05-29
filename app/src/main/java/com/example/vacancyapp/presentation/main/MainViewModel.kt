package com.example.vacancyapp.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacancyapp.domain.models.Vacancy
import com.example.vacancyapp.domain.repository.VacancyRepository
import com.example.vacancyapp.utils.SearchHistoryManager
import com.example.vacancyapp.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FilterState(
    val query: String = "",
    val city: String = "",
    val company: String = "",
    val salaryFrom: String = "",
    val salaryTo: String = ""
)

data class MainUiState(
    val vacancies: List<Vacancy> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val vacancyRepository: VacancyRepository,
    private val tokenManager: TokenManager,
    private val searchHistoryManager: SearchHistoryManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filterState = MutableStateFlow(FilterState())

    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private val _showHistory = MutableStateFlow(false)
    val showHistory = _showHistory.asStateFlow()

    val searchHistory = searchHistoryManager.searchHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filterState = _filterState.asStateFlow()

    val uiState: StateFlow<MainUiState> = combine(
        _searchQuery.debounce(300),
        _filterState
    ) { query, filter -> query to filter }
        .flatMapLatest { (query, filter) ->
            if (query.isBlank() && filter.city.isBlank() && filter.company.isBlank() &&
                filter.salaryFrom.isBlank() && filter.salaryTo.isBlank()) {
                vacancyRepository.getVacancies()
            } else {
                vacancyRepository.searchVacanciesWithFilter(
                    query = query,
                    city = filter.city,
                    company = filter.company,
                    salaryFrom = filter.salaryFrom,
                    salaryTo = filter.salaryTo
                )
            }
        }
        .combine(_isLoading) { vacancies, loading -> Pair(vacancies, loading) }
        .combine(_error) { (vacancies, loading), error ->
            MainUiState(vacancies = vacancies, isLoading = loading, error = error)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState(isLoading = true)
        )

    private val _myVacancies = MutableStateFlow<List<Vacancy>>(emptyList())
    val myVacancies: StateFlow<List<Vacancy>> = _myVacancies.asStateFlow()


    fun onCityChange(city: String) = _filterState.update { it.copy(city = city) }
    fun onCompanyChange(company: String) = _filterState.update { it.copy(company = company) }
    fun onSalaryFromChange(salary: String) = _filterState.update { it.copy(salaryFrom = salary) }
    fun onSalaryToChange(salary: String) = _filterState.update { it.copy(salaryTo = salary) }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _filterState.update { it.copy(query = query) }
        _showHistory.value = query.isBlank()
    }

    fun showHistory() {
        _showHistory.value = true
    }

    fun hideHistory() {
        _showHistory.value = false
    }

    fun onHistoryItemClick(query: String) {
        _searchQuery.value = query
        _filterState.update { it.copy(query = query) }
        _showHistory.value = false

        viewModelScope.launch {
            searchHistoryManager.addQuery(query)
        }
    }

    fun applyFilters() {
        val currentQuery = _filterState.value.query
        _searchQuery.value = currentQuery

        if (currentQuery.isNotBlank()) {
            viewModelScope.launch {
                searchHistoryManager.addQuery(currentQuery)
            }
        }
        _showHistory.value = false
    }

    fun clearHistory() = viewModelScope.launch {
        searchHistoryManager.clearHistory()
        _showHistory.value = false
    }


    fun refresh() = viewModelScope.launch {

        _isLoading.value = true
        _error.value = null

        try {

            val token = tokenManager.token.first() ?: return@launch

            vacancyRepository.syncVacancies(token)
            vacancyRepository.syncFavorites(token)

        } catch (e: Exception) {

            _error.value = e.message ?: "Ошибка загрузки"

        } finally {

            _isLoading.value = false
        }
    }

    fun loadMyVacancies() = viewModelScope.launch {

        _isLoading.value = true

        try {

            val token = tokenManager.token.first()

            if (token == null) {

                _myVacancies.value = emptyList()
                return@launch
            }

            val vacancies = vacancyRepository.getMyVacancies(token)

            _myVacancies.value = vacancies

        } catch (e: Exception) {

            _error.value = e.message
            _myVacancies.value = emptyList()

        } finally {

            _isLoading.value = false
        }
    }

    fun addFavorite(id: Int) = viewModelScope.launch {

        try {

            val token = tokenManager.token.first() ?: return@launch

            vacancyRepository.addFavorite(token, id)

        } catch (e: Exception) {

            _error.value = e.message
        }
    }

    fun removeFavorite(id: Int) = viewModelScope.launch {

        try {

            val token = tokenManager.token.first() ?: return@launch

            vacancyRepository.removeFavorite(token, id)

        } catch (e: Exception) {

            _error.value = e.message
        }
    }

    init {
        refresh()
    }
}