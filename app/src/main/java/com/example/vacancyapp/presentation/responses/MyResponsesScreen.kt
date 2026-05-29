package com.example.vacancyapp.presentation.responses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacancyapp.domain.models.VacancyResponse
import com.example.vacancyapp.domain.repository.ResponseRepository
import com.example.vacancyapp.utils.ResponseStatus
import com.example.vacancyapp.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyResponsesViewModel @Inject constructor(
    private val responseRepository: ResponseRepository,
    private val tokenManager: TokenManager
) : androidx.lifecycle.ViewModel() {
    private val _responses = MutableStateFlow<List<VacancyResponse>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    val responses = _responses.asStateFlow()
    val isLoading = _isLoading.asStateFlow()

    init { load() }

    fun load() = viewModelScope.launch {
        _isLoading.value = true
        try {
            val token = tokenManager.token.first() ?: return@launch
            _responses.value = responseRepository.getMyResponses(token)
        } catch (_: Exception) {
        } finally { _isLoading.value = false }
    }

    fun withdraw(id: Int) = viewModelScope.launch {
        try {
            val token = tokenManager.token.first() ?: return@launch
            responseRepository.deleteResponse(token, id)
            _responses.update { it.filter { r -> r.id != id } }
        } catch (_: Exception) {}
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyResponsesScreen(
    onBack: () -> Unit,
    viewModel: MyResponsesViewModel = hiltViewModel()
) {
    val responses by viewModel.responses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои отклики") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                responses.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("Пока нет откликов", style = MaterialTheme.typography.titleLarge)
                        Text("Ваши отклики на вакансии будут здесь", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(responses, key = { it.id }) { response ->
                            ResponseCard(
                                response = response,
                                onWithdraw = { viewModel.withdraw(response.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResponseCard(
    response: VacancyResponse,
    onWithdraw: () -> Unit
) {
    val statusColor = when (response.status) {
        ResponseStatus.ACCEPTED -> MaterialTheme.colorScheme.primary
        ResponseStatus.REJECTED -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.secondary
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        response.vacancyTitle ?: "Вакансия #${response.vacancyId}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    response.companyName?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
                IconButton(onClick = onWithdraw) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = when (response.status) {
                    ResponseStatus.ACCEPTED -> "Принят"
                    ResponseStatus.REJECTED -> "Отклонён"
                    else -> "На рассмотрении"
                },
                color = statusColor,
                style = MaterialTheme.typography.titleSmall
            )

            response.coverLetter?.let {
                Spacer(Modifier.height(8.dp))
                Text("Сопроводительное письмо:", style = MaterialTheme.typography.labelMedium)
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}