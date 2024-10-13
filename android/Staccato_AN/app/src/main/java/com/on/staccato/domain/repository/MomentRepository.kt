package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.staccato.MomentCreationResponse
import com.on.staccato.domain.model.Moment
import com.on.staccato.domain.model.MomentLocation
import java.time.LocalDateTime

interface MomentRepository {
    suspend fun getMoments(): ResponseResult<List<MomentLocation>>

    suspend fun getMoment(momentId: Long): Result<Moment>

    suspend fun createMoment(
        memoryId: Long,
        staccatoTitle: String,
        placeName: String,
        latitude: Double,
        longitude: Double,
        address: String,
        visitedAt: LocalDateTime,
        momentImageUrls: List<String>,
    ): Result<MomentCreationResponse>

    suspend fun updateMoment(
        momentId: Long,
        staccatoTitle: String,
        placeName: String,
        address: String,
        latitude: Double,
        longitude: Double,
        visitedAt: LocalDateTime,
        memoryId: Long,
        momentImageUrls: List<String>,
    ): Result<Unit>

    suspend fun deleteMoment(momentId: Long): Result<Unit>

    suspend fun updateFeeling(
        momentId: Long,
        feeling: String,
    ): Result<Unit>
}
