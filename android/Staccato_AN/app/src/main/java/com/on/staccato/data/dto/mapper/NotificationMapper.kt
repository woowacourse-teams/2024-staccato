package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.notification.NotificationExistenceResponse
import com.on.staccato.domain.model.notification.NotificationExistence

fun NotificationExistenceResponse.toDomain() =
    NotificationExistence(
        isExist = isExist,
    )
