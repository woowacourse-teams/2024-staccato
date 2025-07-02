package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.staccato.FeelingRequest
import com.on.staccato.domain.model.Feeling

fun Feeling.toFeelingRequest(): FeelingRequest =
    FeelingRequest(
        feeling = value,
    )
