package com.example.vacancyapp.presentation.responses

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
import androidx.lifecycle.viewModelScope
import com.example.vacancyapp.domain.models.VacancyResponse
import com.example.vacancyapp.domain.repository.ResponseRepository
import com.example.vacancyapp.utils.ResponseStatus
import com.example.vacancyapp.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VacancyResponsesViewModel @Inject constructor(
    private val responseRepository: ResponseRepository,
    private val tokenManager: TokenManager
) : androidx.lifecycle.ViewModel() {

    private val _responses = MutableStateFlow<List<VacancyResponse>>(emptyList())
    private val _isLoading = MutableStateFlow(true)

    val responses = _responses.asStateFlow()
    val isLoading = _isLoading.asStateFlow()

    fun load(vacancyId: Int) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val token = tokenManager.token.first() ?: return@launch
            _responses.value = responseRepository.getVacancyResponses(token, vacancyId)
        } catch (_: Exception) {
        } finally {
            _isLoading.value = false
        }
    }

    fun updateStatus(id: Int, status: String) = viewModelScope.launch {
        try {
            val token = tokenManager.token.first() ?: return@launch
            val updated = responseRepository.updateResponseStatus(token, id, status)
            _responses.update { list ->
                list.map { if (it.id == id) updated else it }
            }
        } catch (_: Exception) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacancyResponsesScreen(
    vacancyId: Int,
    onBack: () -> Unit,
    onViewResume: (Int) -> Unit,
    viewModel: VacancyResponsesViewModel = hiltViewModel()
) {
    val responses by viewModel.responses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(vacancyId) {
        viewModel.load(vacancyId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Отклики на вакансию") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                responses.isEmpty() -> {
                    Text(
                        text = "Откликов пока нет",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(responses, key = { it.id }) { response ->
                            ApplicantCard(
                                response = response,
                                onViewResume = { onViewResume(response.userId) },
                                onAccept = { viewModel.updateStatus(response.id, ResponseStatus.ACCEPTED) },
                                onReject = { viewModel.updateStatus(response.id, ResponseStatus.REJECTED) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicantCard(
    response: VacancyResponse,
    onViewResume: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = response.applicantEmail ?: response.applicantEmail ?: "Соискатель #${response.userId}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = when (response.status) {
                    ResponseStatus.ACCEPTED -> "Принят"
                    ResponseStatus.REJECTED -> "Отклонён"
                    else -> "Ожидает рассмотрения"
                },
                color = when (response.status) {
                    ResponseStatus.ACCEPTED -> MaterialTheme.colorScheme.primary
                    ResponseStatus.REJECTED -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.secondary
                }
            )

            response.coverLetter?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Сопроводительное письмо:",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onViewResume,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Посмотреть резюме")
            }

            if (response.status == ResponseStatus.PENDING) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Принять")
                    }
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Отклонить")
                    }
                }
            }
        }
    }
}