package com.example.vacancyapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vacancyapp.presentation.admin.AdminPanelScreen
import com.example.vacancyapp.presentation.addedit.AddEditVacancyScreen
import com.example.vacancyapp.presentation.auth.LoginScreen
import com.example.vacancyapp.presentation.auth.RegisterScreen
import com.example.vacancyapp.presentation.detail.VacancyDetailScreen
import com.example.vacancyapp.presentation.favorites.FavoritesScreen
import com.example.vacancyapp.presentation.main.MainScreen
import com.example.vacancyapp.presentation.responses.MyResponsesScreen
import com.example.vacancyapp.presentation.responses.VacancyResponsesScreen
import com.example.vacancyapp.presentation.resume.ResumeScreen
import com.example.vacancyapp.presentation.resume.ResumeViewScreen
import com.example.vacancyapp.presentation.profile.ProfileScreen

@Composable
fun NavGraph(
    startDestination: String = "login",
    token: String?,
    role: String?,
    userId: Int?
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("main") { popUpTo("login") { inclusive = true } } },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("main") { popUpTo("login") { inclusive = true } } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("main") {
            MainScreen(
                currentTab = "all",
                onVacancyClick = { id -> navController.navigate("detail/$id") },
                onAddVacancy = { navController.navigate("addedit/-1") },
                onNavigateToAll = { },
                onNavigateToFavorites = { navController.navigate("favorites") },
                onNavigateToMyVacancies = { navController.navigate("myvacancies") },
                onNavigateToResume = { navController.navigate("resume") },
                onNavigateToResponses = { navController.navigate("myresponses") },
                onNavigateToAdmin = { navController.navigate("admin") },
                onNavigateToProfile = { navController.navigate("profile") },
                onLogout = { navController.navigate("login") { popUpTo(0) { inclusive = true } } },
                role = role,
                token = token,
                userId = userId
            )
        }

        composable("myvacancies") {
            MainScreen(
                currentTab = "my",
                onVacancyClick = { id -> navController.navigate("detail/$id") },
                onAddVacancy = { navController.navigate("addedit/-1") },
                onNavigateToAll = {
                    navController.navigate("main") {
                        popUpTo("main") { saveState = true }
                    }
                },
                onNavigateToFavorites = { navController.navigate("favorites") },
                onNavigateToMyVacancies = { },
                onNavigateToResume = { navController.navigate("resume") },
                onNavigateToResponses = { navController.navigate("myresponses") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToAdmin = { navController.navigate("admin") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                role = role,
                token = token,
                userId = userId
            )
        }
        composable("favorites") {
            FavoritesScreen(
                onVacancyClick = { id -> navController.navigate("detail/$id") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("resume") {
            ResumeScreen(onBack = { navController.popBackStack() })
        }

        composable("myresponses") {
            MyResponsesScreen(onBack = { navController.popBackStack() })
        }

        composable("admin") {
            AdminPanelScreen(onBack = { navController.popBackStack() })
        }

        composable("profile") {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            VacancyDetailScreen(
                vacancyId = it.arguments!!.getInt("id"),
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate("addedit/$id") },
                onNavigateToResponses = { vacancyId ->
                    navController.navigate("vacancyresponses/$vacancyId")
                },
                onNavigateToResume = { navController.navigate("resume") }
            )
        }

        composable(
            "vacancyresponses/{vacancyId}",
            arguments = listOf(navArgument("vacancyId") { type = NavType.IntType })
        ) {
            VacancyResponsesScreen(
                vacancyId = it.arguments!!.getInt("vacancyId"),
                onBack = { navController.popBackStack() },
                onViewResume = { userId -> navController.navigate("resumeview/$userId") }
            )
        }

        composable(
            "resumeview/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) {
            ResumeViewScreen(
                userId = it.arguments!!.getInt("userId"),
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "addedit/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            AddEditVacancyScreen(
                vacancyId = it.arguments!!.getInt("id").takeIf { it != -1 },
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}