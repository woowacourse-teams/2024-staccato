package com.woowacourse.staccato.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val status: String,
    val message: String,
)
