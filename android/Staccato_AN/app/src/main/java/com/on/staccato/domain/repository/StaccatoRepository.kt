package com.on.staccato.domain.repository

import com.on.staccato.data.dto.staccato.StaccatoCreationResponse
import com.on.staccato.data.network.ApiResult
import com.on.staccato.domain.model.Feeling
import com.on.staccato.domain.model.Staccato
import com.on.staccato.domain.model.StaccatoMarker
import com.on.staccato.domain.model.StaccatoShareLink
import java.time.LocalDateTime

interface StaccatoRepository {
    suspend fun getStaccatoMarkers(): ApiResult<List<StaccatoMarker>>

    suspend fun getStaccato(staccatoId: Long): ApiResult<Staccato>

    suspend fun createStaccatoShareLink(staccatoId: Long): ApiResult<StaccatoShareLink>

    suspend fun createStaccato(
        categoryId: Long,
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
        categoryId: Long,
        staccatoImageUrls: List<String>,
    ): ApiResult<Unit>

    suspend fun deleteStaccato(staccatoId: Long): ApiResult<Unit>

    suspend fun updateFeeling(
        staccatoId: Long,
        feeling: Feeling,
    ): ApiResult<Unit>
}
