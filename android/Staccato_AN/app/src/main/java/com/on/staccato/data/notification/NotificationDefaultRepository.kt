package com.on.staccato.data.notification

import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.handle
import com.on.staccato.domain.model.NotificationExistence
import com.on.staccato.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationDefaultRepository
    @Inject constructor(
        private val notificationApiService: NotificationApiService,
    ) : NotificationRepository {
        override suspend fun getNotificationExistence(): ApiResult<NotificationExistence> =
            notificationApiService.getNotificationExistence().handle { it.toDomain() }
    }
