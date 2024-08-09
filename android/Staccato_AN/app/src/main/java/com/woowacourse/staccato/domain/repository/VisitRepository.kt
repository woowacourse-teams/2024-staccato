package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.dto.visit.VisitCreationResponse
import com.woowacourse.staccato.domain.model.Visit
import okhttp3.MultipartBody
import java.time.LocalDateTime

interface VisitRepository {
    suspend fun getVisit(visitId: Long): Result<Visit>

    suspend fun createVisit(
        travelId: Long,
        placeName: String,
        latitude: String,
        longitude: String,
        address: String,
        visitedAt: LocalDateTime,
        visitImageMultiParts: List<MultipartBody.Part>,
    ): Result<VisitCreationResponse>

    suspend fun updateVisit(
        visitId: Long,
        placeName: String,
        visitImageUrls: List<String>,
        visitImageMultiParts: List<MultipartBody.Part>,
    ): Result<Unit>

    suspend fun deleteVisit(visitId: Long): Result<Unit>
}
