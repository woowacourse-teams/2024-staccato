package com.on.staccato.domain.repository

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.staccato.StaccatoCreationResponse
import com.on.staccato.domain.model.Staccato
import com.on.staccato.domain.model.StaccatoLocation
import java.time.LocalDateTime

interface StaccatoRepository {
    suspend fun getStaccatos(): ApiResult<List<StaccatoLocation>>

    suspend fun getStaccato(staccatoId: Long): ApiResult<Staccato>

    suspend fun createStaccato(
        memoryId: Long,
        staccatoTitle: String,
        placeName: String,
        latitude: Double,
        longitude: Double,
        address: String,
        visitedAt: LocalDateTime,
        staccatoImageUrls: List<String>,
    ): ApiResult<StaccatoCreationResponse>

    suspend fun updateStaccato(
        staccatoId: Long,
        staccatoTitle: String,
        placeName: String,
        address: String,
        latitude: Double,
        longitude: Double,
        visitedAt: LocalDateTime,
        memoryId: Long,
        staccatoImageUrls: List<String>,
    ): ApiResult<Unit>

    suspend fun deleteStaccato(staccatoId: Long): ApiResult<Unit>

    suspend fun updateFeeling(
        staccatoId: Long,
        feeling: String,
    ): ApiResult<Unit>
}
