package com.example.vacancyapp.presentation.addedit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVacancyScreen(
    vacancyId: Int?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddEditVacancyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(vacancyId) { vacancyId?.let { viewModel.loadVacancy(it) } }
    LaunchedEffect(uiState.isSaved) { if (uiState.isSaved) onSaved() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (vacancyId != null) "Редактировать" else "Новая вакансия") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(uiState.title, viewModel::onTitleChange, Modifier.fillMaxWidth(), label = { Text("Название *") }, singleLine = true)
            OutlinedTextField(uiState.company, viewModel::onCompanyChange, Modifier.fillMaxWidth(), label = { Text("Компания *") }, singleLine = true)
            OutlinedTextField(uiState.salary, viewModel::onSalaryChange, Modifier.fillMaxWidth(), label = { Text("Зарплата") }, singleLine = true)
            OutlinedTextField(uiState.location, viewModel::onLocationChange, Modifier.fillMaxWidth(), label = { Text("Локация") }, singleLine = true)
            OutlinedTextField(uiState.description, viewModel::onDescriptionChange, Modifier.fillMaxWidth().height(150.dp), label = { Text("Описание") })

            uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = { viewModel.save(vacancyId) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) CircularProgressIndicator(Modifier.size(20.dp))
                else Text("Сохранить")
            }
        }
    }
}