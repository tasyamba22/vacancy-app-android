package com.example.vacancyapp.presentation.resume

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeScreen(
    onBack: () -> Unit,
    viewModel: ResumeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) { if (uiState.isSaved) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Моё резюме") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.fullName,
                        onValueChange = viewModel::onFullNameChange,
                        label = { Text("ФИО *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.phone,
                        onValueChange = viewModel::onPhoneChange,
                        label = { Text("Телефон") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.skills,
                        onValueChange = viewModel::onSkillsChange,
                        label = { Text("Навыки") },
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )

                    OutlinedTextField(
                        value = uiState.experience,
                        onValueChange = viewModel::onExperienceChange,
                        label = { Text("Опыт работы") },
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )

                    OutlinedTextField(
                        value = uiState.education,
                        onValueChange = viewModel::onEducationChange,
                        label = { Text("Образование") },
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )

                    uiState.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }

                    Button(
                        onClick = viewModel::save,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    ) {
                        Text(if (uiState.resume == null) "Создать резюме" else "Сохранить изменения")
                    }

                    if (uiState.resume != null) {
                        OutlinedButton(
                            onClick = viewModel::delete,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Удалить резюме")
                        }
                    }
                }
            }
        }
    }
}