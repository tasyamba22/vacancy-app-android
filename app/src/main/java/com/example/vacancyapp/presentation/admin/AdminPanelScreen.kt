package com.example.vacancyapp.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(onBack: () -> Unit, viewModel: AdminViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Админ-панель") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } })
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        uiState.stats?.let { stats ->
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("Статистика", style = MaterialTheme.typography.titleLarge)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Пользователей: ${stats.totalUsers}")
                                    Text("Вакансий: ${stats.totalVacancies}")
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("Пользователи", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                    }
                    items(uiState.users, key = { it.id }) { user ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(user.email, style = MaterialTheme.typography.titleSmall)
                                Text("Роль: ${user.role} | ${if (user.isBlocked) "Заблокирован" else "Активен"}", style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (user.isBlocked) {
                                        OutlinedButton(onClick = { viewModel.unblockUser(user.id) }) { Text("Разблокировать") }
                                    } else {
                                        OutlinedButton(onClick = { viewModel.blockUser(user.id) }, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Блокировать") }
                                    }
                                    val newRole = if (user.role == "ADMIN") "USER" else "ADMIN"
                                    OutlinedButton(onClick = { viewModel.changeRole(user.id, newRole) }) {
                                        Text(if (user.role == "ADMIN") "Сделать USER" else "Сделать ADMIN")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}