package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.moment.MomentCreationResponse
import com.woowacourse.staccato.domain.model.Moment
import com.woowacourse.staccato.domain.model.MomentLocation
import okhttp3.MultipartBody
import java.time.LocalDateTime

interface MomentRepository {
    suspend fun getMoments(): ResponseResult<List<MomentLocation>>

    suspend fun getMoment(momentId: Long): Result<Moment>

    suspend fun createMoment(
        memoryId: Long,
        placeName: String,
        latitude: String,
        longitude: String,
        address: String,
        visitedAt: LocalDateTime,
        momentImageMultiParts: List<MultipartBody.Part>,
    ): Result<MomentCreationResponse>

    suspend fun updateMoment(
        momentId: Long,
        placeName: String,
        momentImageUrls: List<String>,
        momentImageMultiParts: List<MultipartBody.Part>,
    ): Result<Unit>

    suspend fun deleteMoment(momentId: Long): Result<Unit>
}
