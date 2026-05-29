package com.example.vacancyapp.di

import com.example.vacancyapp.data.repository.*
import com.example.vacancyapp.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindVacancyRepository(impl: VacancyRepositoryImpl): VacancyRepository

    @Binds @Singleton
    abstract fun bindResumeRepository(impl: ResumeRepositoryImpl): ResumeRepository

    @Binds @Singleton
    abstract fun bindResponseRepository(impl: ResponseRepositoryImpl): ResponseRepository

    @Binds @Singleton
    abstract fun bindAdminRepository(impl: AdminRepositoryImpl): AdminRepository
}