package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.moment.FeelingRequest
import com.woowacourse.staccato.domain.model.Feeling

fun Feeling.toFeelingRequest(): FeelingRequest =
    FeelingRequest(
        feeling = value,
    )
