package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.dto.moment.MomentCreationResponse
import com.woowacourse.staccato.domain.model.Moment
import java.time.LocalDateTime

interface MomentRepository {
    suspend fun getMoment(momentId: Long): Result<Moment>

    suspend fun createMoment(
        memoryId: Long,
        placeName: String,
        latitude: String,
        longitude: String,
        address: String,
        visitedAt: LocalDateTime,
        momentImageUrls: List<String>,
    ): Result<MomentCreationResponse>

    suspend fun updateMoment(
        momentId: Long,
        placeName: String,
        momentImageUrls: List<String>,
    ): Result<Unit>

    suspend fun deleteMoment(momentId: Long): Result<Unit>

    suspend fun updateFeeling(
        momentId: Long,
        feeling: String,
    ): Result<Unit>
}
