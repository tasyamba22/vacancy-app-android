package com.example.vacancyapp.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vacancyapp.utils.ResponseStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacancyDetailScreen(
    vacancyId: Int,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    onNavigateToResume: () -> Unit,
    onNavigateToResponses: (Int) -> Unit,
    viewModel: VacancyDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showResponseDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var coverLetter by remember { mutableStateOf("") }

    LaunchedEffect(vacancyId) { viewModel.load(vacancyId) }

    if (showResponseDialog) {
        AlertDialog(
            onDismissRequest = { showResponseDialog = false },
            title = { Text("Откликнуться на вакансию") },
            text = {
                Column {
                    Text("Сопроводительное письмо (необязательно):")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = coverLetter,
                        onValueChange = { coverLetter = it },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        placeholder = { Text("Расскажите, почему вы подходите...") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.respond(vacancyId, coverLetter.ifBlank { null })
                    showResponseDialog = false
                }) { Text("Отправить отклик") }
            },
            dismissButton = {
                TextButton(onClick = { showResponseDialog = false }) { Text("Отмена") }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удаление вакансии") },
            text = { Text("Вы действительно хотите удалить вакансию?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteVacancy(vacancyId) {
                            onBack()
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Вакансия") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Назад") } },
                actions = {
                    if (uiState.isOwner) {
                        IconButton(onClick = { onEdit(vacancyId) }) {
                            Icon(Icons.Default.Edit, "Редактировать")
                        }

                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Удалить")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                uiState.vacancy == null -> Text("Вакансия не найдена", Modifier.align(Alignment.Center))
                else -> {
                    val vacancy = uiState.vacancy!!

                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(vacancy.title, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(Modifier.height(8.dp))
                        Text(vacancy.company, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)

                        Spacer(Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Зарплата", style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        vacancy.salary ?: "Не указана",
                                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Локация", style = MaterialTheme.typography.labelMedium)
                                    Text(vacancy.location ?: "Не указана", style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Text("Описание", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                        Spacer(Modifier.height(8.dp))
                        Text(vacancy.description ?: "Описание отсутствует", style = MaterialTheme.typography.bodyLarge, lineHeight = 22.sp)

                        Spacer(Modifier.height(32.dp))

                        when {
                            uiState.isOwner -> {
                                Button(onClick = { onNavigateToResponses(vacancyId) }, modifier = Modifier.fillMaxWidth()) {
                                    Icon(Icons.Default.People, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Посмотреть отклики")
                                }
                            }
                            uiState.myResume == null -> {
                                OutlinedButton(onClick = onNavigateToResume, modifier = Modifier.fillMaxWidth()) {
                                    Text("Создайте резюме для отклика")
                                }
                            }
                            uiState.myResponse != null -> {
                                val status = uiState.myResponse!!.status
                                val (text, color) = when (status) {
                                    ResponseStatus.ACCEPTED -> "Отклик принят" to MaterialTheme.colorScheme.primary
                                    ResponseStatus.REJECTED -> "Отклик отклонён" to MaterialTheme.colorScheme.error
                                    else -> "Отклик отправлен (на рассмотрении)" to MaterialTheme.colorScheme.secondary
                                }
                                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
                                    Text(text, Modifier.padding(16.dp), color = color, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                            else -> {
                                Button(onClick = { showResponseDialog = true }, modifier = Modifier.fillMaxWidth()) {
                                    Icon(Icons.Default.Send, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Откликнуться на вакансию")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}