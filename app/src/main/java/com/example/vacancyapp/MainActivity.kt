package com.example.vacancyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.vacancyapp.presentation.navigation.NavGraph
import com.example.vacancyapp.presentation.theme.VacancyAppTheme
import com.example.vacancyapp.utils.ThemeManager
import com.example.vacancyapp.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var tokenManager: TokenManager
    @Inject lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDark by themeManager.isDarkTheme.collectAsState(initial = false)
            val token by tokenManager.token.collectAsState(initial = null)
            val role by tokenManager.role.collectAsState(initial = null)
            val userId by tokenManager.userId.collectAsState(initial = null)

            VacancyAppTheme(darkTheme = isDark) {
                NavGraph(
                    startDestination = if (token != null) "main" else "login",
                    token = token,
                    role = role,
                    userId = userId
                )
            }
        }
    }
}