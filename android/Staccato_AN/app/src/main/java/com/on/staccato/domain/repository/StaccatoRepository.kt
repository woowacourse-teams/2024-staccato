package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.staccato.StaccatoCreationResponse
import com.on.staccato.domain.model.Staccato
import com.on.staccato.domain.model.StaccatoLocation
import java.time.LocalDateTime

interface StaccatoRepository {
    suspend fun getStaccatos(): ResponseResult<List<StaccatoLocation>>

    suspend fun getStaccato(staccatoId: Long): ResponseResult<Staccato>

    suspend fun createStaccato(
        categoryId: Long,
        staccatoTitle: String,
        placeName: String,
        latitude: Double,
        longitude: Double,
        address: String,
        visitedAt: LocalDateTime,
        staccatoImageUrls: List<String>,
    ): ResponseResult<StaccatoCreationResponse>

    suspend fun updateStaccato(
        staccatoId: Long,
        staccatoTitle: String,
        placeName: String,
        address: String,
        latitude: Double,
        longitude: Double,
        visitedAt: LocalDateTime,
        categoryId: Long,
        staccatoImageUrls: List<String>,
    ): ResponseResult<Unit>

    suspend fun deleteStaccato(staccatoId: Long): ResponseResult<Unit>

    suspend fun updateFeeling(
        staccatoId: Long,
        feeling: String,
    ): ResponseResult<Unit>
}
