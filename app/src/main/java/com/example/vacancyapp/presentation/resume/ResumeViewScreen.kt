package com.example.vacancyapp.presentation.resume

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacancyapp.R
import com.example.vacancyapp.domain.models.Resume
import com.example.vacancyapp.domain.repository.ResumeRepository
import com.example.vacancyapp.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResumeViewViewModel @Inject constructor(
    private val resumeRepository: ResumeRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _resume = MutableStateFlow<Resume?>(null)
    private val _isLoading = MutableStateFlow(true)

    val resume = _resume.asStateFlow()
    val isLoading = _isLoading.asStateFlow()

    fun load(userId: Int) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val token = tokenManager.token.first() ?: return@launch
            _resume.value = resumeRepository.getResumeByUserId(token, userId)
        } catch (_: Exception) {
        } finally {
            _isLoading.value = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeViewScreen(userId: Int, onBack: () -> Unit, viewModel: ResumeViewViewModel = hiltViewModel()) {
    val resume by viewModel.resume.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    LaunchedEffect(userId) { viewModel.load(userId) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(R.string.resume_view_title)) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, stringResource(R.string.resume_view_cd_back))
                }
            }
        )
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                resume == null -> Text(stringResource(R.string.resume_view_not_found), Modifier.align(Alignment.Center))
                else -> Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    resume!!.let { r ->
                        Text(r.fullName, style = MaterialTheme.typography.headlineMedium)
                        r.phone?.let { Text(" $it") }
                        r.skills?.let {
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(stringResource(R.string.resume_view_section_skills), style = MaterialTheme.typography.titleSmall)
                                    Text(it)
                                }
                            }
                        }
                        r.experience?.let {
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(stringResource(R.string.resume_view_section_experience), style = MaterialTheme.typography.titleSmall)
                                    Text(it)
                                }
                            }
                        }
                        r.education?.let {
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(stringResource(R.string.resume_view_section_education), style = MaterialTheme.typography.titleSmall)
                                    Text(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}