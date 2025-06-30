package com.on.staccato

import com.on.staccato.domain.ExceptionType
import com.on.staccato.presentation.R

fun ExceptionType.toMessageId(): Int =
    when (this) {
        ExceptionType.REQUIRED_VALUES -> R.string.error_message_required_values
        ExceptionType.NETWORK -> R.string.error_message_network
        ExceptionType.UNKNOWN -> R.string.error_message_unknown
        ExceptionType.IMAGE_UPLOAD -> R.string.error_message_image_upload
    }
