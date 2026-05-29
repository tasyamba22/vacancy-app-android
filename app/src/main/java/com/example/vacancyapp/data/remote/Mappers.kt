package com.example.vacancyapp.data.remote

import com.example.vacancyapp.data.local.VacancyEntity
import com.example.vacancyapp.data.remote.dto.*
import com.example.vacancyapp.domain.models.*

object Mappers {


    fun VacancyDto.toDomain(): Vacancy = Vacancy(
        id = id,
        userId = userId,
        title = title,
        company = company,
        salary = salary,
        location = location,
        description = description,
        createdAt = createdAt,
        isFavorite = isFavorite
    )

    fun VacancyDto.toEntity(): VacancyEntity = VacancyEntity(
        id = id,
        userId = userId,
        title = title,
        company = company,
        salary = salary,
        location = location,
        description = description,
        createdAt = createdAt,
        isFavorite = isFavorite
    )

    fun VacancyEntity.toDomain(): Vacancy = Vacancy(
        id = id,
        userId = userId,
        title = title,
        company = company,
        salary = salary,
        location = location,
        description = description,
        createdAt = createdAt,
        isFavorite = isFavorite
    )

    fun ResumeDto.toDomain(): Resume = Resume(
        id = id, userId = userId, fullName = fullName, phone = phone,
        skills = skills, experience = experience, education = education,
        createdAt = createdAt, updatedAt = updatedAt
    )

    fun ResponseDto.toDomain(): VacancyResponse = VacancyResponse(
        id = id, userId = userId, vacancyId = vacancyId, resumeId = resumeId,
        status = status, coverLetter = coverLetter, createdAt = createdAt,
        updatedAt = updatedAt, vacancyTitle = vacancyTitle,
        companyName = companyName, applicantEmail = applicantEmail
    )

    fun UserDto.toDomain(): User = User(
        id = id, email = email, role = role,
        isBlocked = isBlocked, createdAt = createdAt
    )
}