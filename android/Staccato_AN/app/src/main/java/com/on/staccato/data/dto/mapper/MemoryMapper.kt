package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.category.CategoriesResponse
import com.on.staccato.data.dto.category.CategoryRequest
import com.on.staccato.data.dto.category.CategoryResponse
import com.on.staccato.data.dto.category.CategoryStaccatoDto
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.CategoryCandidate
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.MemoryStaccato
import com.on.staccato.domain.model.NewMemory
import java.time.LocalDate
import java.time.LocalDateTime

fun CategoryResponse.toDomain() =
    Category(
        categoryId = categoryId,
        categoryThumbnailUrl = categoryThumbnailUrl,
        categoryTitle = categoryTitle,
        startAt = startAt?.let { LocalDate.parse(startAt) },
        endAt = endAt?.let { LocalDate.parse(endAt) },
        description = description,
        mates = mates.map { it.toDomain() },
        staccatos = staccatos.map { it.toDomain() },
    )

fun CategoriesResponse.toDomain(): CategoryCandidates =
    CategoryCandidates(
        this.categories.map {
            CategoryCandidate(
                categoryId = it.categoryId,
                categoryTitle = it.categoryTitle,
                startAt = LocalDate.parse(it.startAt),
                endAt = LocalDate.parse(it.endAt),
            )
        },
    )

fun CategoryStaccatoDto.toDomain() =
    MemoryStaccato(
        staccatoId = staccatoId,
        staccatoTitle = staccatoTitle,
        staccatoImageUrl = staccatoImageUrl,
        visitedAt = LocalDateTime.parse(visitedAt),
    )

fun NewMemory.toDto() =
    CategoryRequest(
        categoryThumbnailUrl = memoryThumbnailUrl,
        categoryTitle = memoryTitle,
        description = description,
        startAt = startAt?.toString(),
        endAt = endAt?.toString(),
    )
