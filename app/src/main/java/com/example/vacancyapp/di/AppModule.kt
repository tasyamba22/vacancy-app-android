package com.example.vacancyapp.di

import android.content.Context
import androidx.room.Room
import com.example.vacancyapp.data.local.AppDatabase
import com.example.vacancyapp.data.local.VacancyDao
import com.example.vacancyapp.domain.repository.AuthRepository
import com.example.vacancyapp.domain.repository.ResponseRepository
import com.example.vacancyapp.domain.repository.ResumeRepository
import com.example.vacancyapp.domain.repository.VacancyRepository
import com.example.vacancyapp.domain.usecases.*
import com.example.vacancyapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
        install(Logging) { level = LogLevel.BODY }
        defaultRequest {
            url(Constants.BASE_URL)
            contentType(ContentType.Application.Json)
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "vacancy_db").build()

    @Provides
    fun provideVacancyDao(db: AppDatabase): VacancyDao = db.vacancyDao()

    @Provides
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase = LoginUseCase(repository)

    @Provides
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase = RegisterUseCase(repository)

    @Provides
    fun provideLogoutUseCase(repository: AuthRepository): LogoutUseCase = LogoutUseCase(repository)

    @Provides
    fun provideGetVacanciesUseCase(repository: VacancyRepository): GetVacanciesUseCase = GetVacanciesUseCase(repository)

    @Provides
    fun provideSearchVacanciesUseCase(repository: VacancyRepository): SearchVacanciesUseCase = SearchVacanciesUseCase(repository)

    @Provides
    fun provideGetMyVacanciesUseCase(repository: VacancyRepository): GetMyVacanciesUseCase = GetMyVacanciesUseCase(repository)

    @Provides
    fun provideGetFavoritesUseCase(repository: VacancyRepository): GetFavoritesUseCase = GetFavoritesUseCase(repository)

    @Provides
    fun provideCreateVacancyUseCase(repository: VacancyRepository): CreateVacancyUseCase = CreateVacancyUseCase(repository)

    @Provides
    fun provideUpdateVacancyUseCase(repository: VacancyRepository): UpdateVacancyUseCase = UpdateVacancyUseCase(repository)

    @Provides
    fun provideDeleteVacancyUseCase(repository: VacancyRepository): DeleteVacancyUseCase = DeleteVacancyUseCase(repository)

    @Provides
    fun provideSyncVacanciesUseCase(repository: VacancyRepository): SyncVacanciesUseCase = SyncVacanciesUseCase(repository)

    @Provides
    fun provideAddFavoriteUseCase(repository: VacancyRepository): AddFavoriteUseCase = AddFavoriteUseCase(repository)

    @Provides
    fun provideRemoveFavoriteUseCase(repository: VacancyRepository): RemoveFavoriteUseCase = RemoveFavoriteUseCase(repository)

    @Provides
    fun provideGetMyResumeUseCase(repository: ResumeRepository): GetMyResumeUseCase = GetMyResumeUseCase(repository)

    @Provides
    fun provideGetResumeByUserIdUseCase(repository: ResumeRepository): GetResumeByUserIdUseCase = GetResumeByUserIdUseCase(repository)

    @Provides
    fun provideCreateResumeUseCase(repository: ResumeRepository): CreateResumeUseCase = CreateResumeUseCase(repository)

    @Provides
    fun provideUpdateResumeUseCase(repository: ResumeRepository): UpdateResumeUseCase = UpdateResumeUseCase(repository)

    @Provides
    fun provideDeleteResumeUseCase(repository: ResumeRepository): DeleteResumeUseCase = DeleteResumeUseCase(repository)

    @Provides
    fun provideCreateResponseUseCase(repository: ResponseRepository): CreateResponseUseCase = CreateResponseUseCase(repository)

    @Provides
    fun provideGetMyResponsesUseCase(repository: ResponseRepository): GetMyResponsesUseCase = GetMyResponsesUseCase(repository)

    @Provides
    fun provideGetVacancyResponsesUseCase(repository: ResponseRepository): GetVacancyResponsesUseCase = GetVacancyResponsesUseCase(repository)

    @Provides
    fun provideUpdateResponseStatusUseCase(repository: ResponseRepository): UpdateResponseStatusUseCase = UpdateResponseStatusUseCase(repository)

    @Provides
    fun provideDeleteResponseUseCase(repository: ResponseRepository): DeleteResponseUseCase = DeleteResponseUseCase(repository)
}