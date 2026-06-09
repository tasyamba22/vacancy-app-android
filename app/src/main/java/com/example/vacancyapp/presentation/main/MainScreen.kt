package com.example.vacancyapp.presentation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vacancyapp.R
import com.example.vacancyapp.domain.models.Vacancy
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    currentTab: String = "all",
    onVacancyClick: (Int) -> Unit,
    onAddVacancy: () -> Unit,
    onNavigateToAll: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToMyVacancies: () -> Unit,
    onNavigateToResume: () -> Unit,
    onNavigateToResponses: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    role: String?,
    token: String?,
    userId: Int?,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val myVacancies by viewModel.myVacancies.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val showHistory by viewModel.showHistory.collectAsState()
    val isMyTab = currentTab == "my"

    val displayVacancies = if (isMyTab) myVacancies else uiState.vacancies

    LaunchedEffect(currentTab) {
        if (currentTab == "my") viewModel.loadMyVacancies()
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(if (isMyTab) stringResource(R.string.main_title_my_vacancies)
                        else stringResource(R.string.main_title_vacancies))
                    },
                    actions = {
                        IconButton(onClick = onNavigateToProfile) {
                            Icon(Icons.Default.AccountCircle, stringResource(R.string.main_profile_cd))
                        }
                    }
                )
                if (!isMyTab) {
                    SearchAndFilterBar(
                        filterState = filterState,
                        searchHistory = searchHistory,
                        showHistory = showHistory,
                        onQueryChange = viewModel::onSearchQueryChange,
                        onCityChange = viewModel::onCityChange,
                        onCompanyChange = viewModel::onCompanyChange,
                        onSalaryFromChange = viewModel::onSalaryFromChange,
                        onSalaryToChange = viewModel::onSalaryToChange,
                        onApply = viewModel::applyFilters,
                        onHistoryItemClick = viewModel::onHistoryItemClick,
                        onClearHistory = viewModel::clearHistory,
                        onHideHistory = viewModel::hideHistory
                    )
                }
            }
        },
        floatingActionButton = {
            if (!isMyTab) {
                FloatingActionButton(onClick = onAddVacancy) {
                    Icon(Icons.Default.Add, stringResource(R.string.main_add_cd))
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = !isMyTab,
                    onClick = onNavigateToAll,
                    icon = { Icon(Icons.Default.Work, contentDescription = null) },
                    label = { Text(stringResource(R.string.main_tab_all), style = MaterialTheme.typography.labelSmall) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToFavorites,
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text(stringResource(R.string.main_tab_favorites), style = MaterialTheme.typography.labelSmall) }
                )
                NavigationBarItem(
                    selected = isMyTab,
                    onClick = onNavigateToMyVacancies,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(stringResource(R.string.main_tab_my), style = MaterialTheme.typography.labelSmall) }
                )
                if (role != "ADMIN") {
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToResume,
                        icon = { Icon(Icons.Default.Description, contentDescription = null) },
                        label = { Text(stringResource(R.string.main_tab_resume), style = MaterialTheme.typography.labelSmall) }
                    )
                }
                if (role != "ADMIN") {
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToResponses,
                        icon = { Icon(Icons.Default.Send, contentDescription = null) },
                        label = { Text(stringResource(R.string.main_tab_responses), style = MaterialTheme.typography.labelSmall) }
                    )
                }
                if (role == "ADMIN") {
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToAdmin,
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
                        label = { Text(stringResource(R.string.main_tab_admin), style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                uiState.error != null -> {
                    Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.main_error_load), color = MaterialTheme.colorScheme.error)
                        Button(onClick = viewModel::refresh) {
                            Text(stringResource(R.string.main_refresh))
                        }
                    }
                }
                displayVacancies.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.main_empty))
                    }
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayVacancies, key = { it.id }) { vacancy ->
                        VacancyCard(
                            vacancy = vacancy,
                            onClick = { onVacancyClick(vacancy.id) },
                            onFavoriteClick = {
                                if (vacancy.isFavorite) viewModel.removeFavorite(vacancy.id)
                                else viewModel.addFavorite(vacancy.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VacancyCard(
    vacancy: Vacancy,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vacancy.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(6.dp))

                Text(
                    text = vacancy.company,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                Row {
                    vacancy.salary?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    vacancy.location?.let {
                        Text(
                            text = " $it",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            IconButton(onClick = onFavoriteClick) {
                Icon(
                    if (vacancy.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (vacancy.isFavorite)
                        stringResource(R.string.vacancy_card_unfavorite_cd)
                    else
                        stringResource(R.string.vacancy_card_favorite_cd),
                    tint = if (vacancy.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SearchAndFilterBar(
    filterState: FilterState,
    searchHistory: List<String>,
    showHistory: Boolean,
    onQueryChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onCompanyChange: (String) -> Unit,
    onSalaryFromChange: (String) -> Unit,
    onSalaryToChange: (String) -> Unit,
    onApply: () -> Unit,
    onHistoryItemClick: (String) -> Unit,
    onClearHistory: () -> Unit,
    onHideHistory: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showFilters by remember { mutableStateOf(false) }

    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        OutlinedTextField(
            value = filterState.query,
            onValueChange = onQueryChange,
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused && filterState.query.isBlank()) {
                        onQueryChange("")
                    }
                },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                if (filterState.query.isNotEmpty()) {
                    IconButton(onClick = {
                        onQueryChange("")
                        onHideHistory()
                        focusManager.clearFocus()
                    }) {
                        Icon(Icons.Default.Clear, stringResource(R.string.search_clear))
                    }
                } else {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, stringResource(R.string.search_filters))
                    }
                }
            }
        )

        AnimatedVisibility(visible = showHistory && searchHistory.isNotEmpty() && filterState.query.isBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.search_history_title), style = MaterialTheme.typography.titleSmall)
                        TextButton(onClick = onClearHistory) {
                            Text(stringResource(R.string.search_history_clear))
                        }
                    }
                    HorizontalDivider()
                    searchHistory.forEach { query ->
                        ListItem(
                            headlineContent = { Text(query) },
                            leadingContent = { Icon(Icons.Default.History, null) },
                            modifier = Modifier.clickable {
                                onHistoryItemClick(query)
                                focusManager.clearFocus()
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

        if (showFilters) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = filterState.city,
                onValueChange = onCityChange,
                placeholder = { Text(stringResource(R.string.filter_city_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.LocationOn, null) }
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = filterState.company,
                onValueChange = onCompanyChange,
                placeholder = { Text(stringResource(R.string.filter_company_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = filterState.salaryFrom,
                    onValueChange = onSalaryFromChange,
                    placeholder = { Text(stringResource(R.string.filter_salary_from)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = filterState.salaryTo,
                    onValueChange = onSalaryToChange,
                    placeholder = { Text(stringResource(R.string.filter_salary_to)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onApply(); showFilters = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.filter_apply))
            }
        }
    }
}