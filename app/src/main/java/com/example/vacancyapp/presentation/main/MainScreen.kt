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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
                    title = { Text(if (isMyTab) "Мои вакансии" else "Вакансии") },
                    actions = {
                        IconButton(onClick = onNavigateToProfile) {
                            Icon(Icons.Default.AccountCircle, "Профиль")
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
                    Icon(Icons.Default.Add, "Добавить")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = !isMyTab,
                    onClick = onNavigateToAll,
                    icon = { Icon(Icons.Default.Work, contentDescription = null) },
                    label = { Text("Все", style = MaterialTheme.typography.labelSmall) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToFavorites,
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("Избранное", style = MaterialTheme.typography.labelSmall) }
                )
                NavigationBarItem(
                    selected = isMyTab,
                    onClick = onNavigateToMyVacancies,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Мои", style = MaterialTheme.typography.labelSmall) }
                )
                if (role != "ADMIN") {
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToResume,
                        icon = { Icon(Icons.Default.Description, contentDescription = null) },
                        label = { Text("Резюме", style = MaterialTheme.typography.labelSmall) }
                    )
                }
                if (role != "ADMIN") {
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToResponses,
                        icon = { Icon(Icons.Default.Send, contentDescription = null) },
                        label = { Text("Отклики", style = MaterialTheme.typography.labelSmall) }
                    )
                }
                if (role == "ADMIN") {
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToAdmin,
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
                        label = { Text("Админ", style = MaterialTheme.typography.labelSmall) }
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
                        Text("Ошибка загрузки", color = MaterialTheme.colorScheme.error)
                        Button(onClick = viewModel::refresh) { Text("Обновить") }
                    }
                }
                displayVacancies.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ничего не найдено")
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
                    contentDescription = null,
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
            placeholder = { Text("Поиск вакансий...") },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused && filterState.query.isBlank()) {
                        onQueryChange("") // принудительно показываем историю
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
                        Icon(Icons.Default.Clear, "Очистить")
                    }
                } else {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, "Фильтры")
                    }
                }
            }
        )

        // История поиска
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
                        Text("История поиска", style = MaterialTheme.typography.titleSmall)
                        TextButton(onClick = onClearHistory) {
                            Text("Очистить")
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

        // Панель фильтров
        if (showFilters) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = filterState.city,
                onValueChange = onCityChange,
                placeholder = { Text("Город") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.LocationOn, null) }
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = filterState.company,
                onValueChange = onCompanyChange,
                placeholder = { Text("Компания") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = filterState.salaryFrom,
                    onValueChange = onSalaryFromChange,
                    placeholder = { Text("Зарплата от") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = filterState.salaryTo,
                    onValueChange = onSalaryToChange,
                    placeholder = { Text("до") },
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
                Text("Применить фильтры")
            }
        }
    }
}