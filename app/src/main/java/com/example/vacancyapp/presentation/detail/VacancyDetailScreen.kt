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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vacancyapp.R
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
            title = { Text(stringResource(R.string.respond_dialog_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.respond_dialog_cover_letter_label))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = coverLetter,
                        onValueChange = { coverLetter = it },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        placeholder = { Text(stringResource(R.string.respond_dialog_cover_letter_hint)) }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.respond(vacancyId, coverLetter.ifBlank { null })
                    showResponseDialog = false
                }) { Text(stringResource(R.string.respond_dialog_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showResponseDialog = false }) {
                    Text(stringResource(R.string.respond_dialog_cancel))
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_dialog_title)) },
            text = { Text(stringResource(R.string.delete_dialog_message)) },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteVacancy(vacancyId) { onBack() }
                    showDeleteDialog = false
                }) { Text(stringResource(R.string.delete_dialog_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.delete_dialog_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.detail_cd_back))
                    }
                },
                actions = {
                    if (uiState.isOwner) {
                        IconButton(onClick = { onEdit(vacancyId) }) {
                            Icon(Icons.Default.Edit, stringResource(R.string.detail_cd_edit))
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, stringResource(R.string.detail_cd_delete))
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                uiState.vacancy == null -> Text(
                    stringResource(R.string.detail_not_found),
                    Modifier.align(Alignment.Center)
                )
                else -> {
                    val vacancy = uiState.vacancy!!
                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            vacancy.title,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            vacancy.company,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(stringResource(R.string.detail_salary_label), style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        vacancy.salary ?: stringResource(R.string.detail_salary_not_set),
                                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(stringResource(R.string.detail_location_label), style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        vacancy.location ?: stringResource(R.string.detail_location_not_set),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        Text(
                            stringResource(R.string.detail_description_label),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            vacancy.description ?: stringResource(R.string.detail_description_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 22.sp
                        )
                        Spacer(Modifier.height(32.dp))

                        when {
                            uiState.isOwner -> {
                                Button(
                                    onClick = { onNavigateToResponses(vacancyId) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.People, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(R.string.detail_button_responses))
                                }
                            }
                            uiState.myResume == null -> {
                                OutlinedButton(
                                    onClick = onNavigateToResume,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(stringResource(R.string.detail_button_create_resume))
                                }
                            }
                            uiState.myResponse != null -> {
                                val status = uiState.myResponse!!.status
                                val (text, color) = when (status) {
                                    ResponseStatus.ACCEPTED -> stringResource(R.string.detail_status_accepted) to MaterialTheme.colorScheme.primary
                                    ResponseStatus.REJECTED -> stringResource(R.string.detail_status_rejected) to MaterialTheme.colorScheme.error
                                    else -> stringResource(R.string.detail_status_pending) to MaterialTheme.colorScheme.secondary
                                }
                                Card(
                                    Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
                                ) {
                                    Text(text, Modifier.padding(16.dp), color = color, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                            else -> {
                                Button(
                                    onClick = { showResponseDialog = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Send, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(R.string.detail_button_respond))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}