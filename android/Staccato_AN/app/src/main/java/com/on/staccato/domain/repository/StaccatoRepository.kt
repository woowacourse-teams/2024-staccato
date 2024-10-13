package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.staccato.StaccatoCreationResponse
import com.on.staccato.domain.model.Moment
import com.on.staccato.domain.model.MomentLocation
import java.time.LocalDateTime

interface StaccatoRepository {
    suspend fun getStaccatos(): ResponseResult<List<MomentLocation>>

    suspend fun getStaccato(staccatoId: Long): Result<Moment>

    suspend fun createStaccato(
        memoryId: Long,
        staccatoTitle: String,
        placeName: String,
        latitude: Double,
        longitude: Double,
        address: String,
        visitedAt: LocalDateTime,
        staccatoImageUrls: List<String>,
    ): Result<StaccatoCreationResponse>

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
    ): Result<Unit>

    suspend fun deleteStaccato(staccatoId: Long): Result<Unit>

    suspend fun updateFeeling(
        staccatoId: Long,
        feeling: String,
    ): Result<Unit>
}
