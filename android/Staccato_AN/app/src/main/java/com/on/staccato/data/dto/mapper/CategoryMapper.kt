package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.category.CategoriesResponse
import com.on.staccato.data.dto.category.CategoryRequest
import com.on.staccato.data.dto.category.CategoryResponse
import com.on.staccato.data.dto.category.CategoryStaccatoDto
import com.on.staccato.data.dto.member.ParticipantDto
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.CategoryCandidate
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.CategoryStaccato
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.NewCategory
import com.on.staccato.domain.model.Participant
import com.on.staccato.domain.model.Role
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
        color = color,
        mates = participants.map { it.toDomain() },
        staccatos = staccatos.map { it.toDomain() },
        isShared = isShared,
        myRole = Role.of(myRole),
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
    CategoryStaccato(
        staccatoId = staccatoId,
        staccatoTitle = staccatoTitle,
        staccatoImageUrl = staccatoImageUrl,
        visitedAt = LocalDateTime.parse(visitedAt),
    )

fun NewCategory.toDto() =
    CategoryRequest(
        categoryThumbnailUrl = categoryThumbnailUrl,
        categoryTitle = categoryTitle,
        description = description,
        startAt = startAt?.toString(),
        endAt = endAt?.toString(),
        color = color,
    )

fun ParticipantDto.toDomain(): Participant =
    Participant(
        member =
            Member(
                memberId = id,
                nickname = nickname,
                memberImage = imageUrl,
            ),
        role = Role.of(role),
    )
