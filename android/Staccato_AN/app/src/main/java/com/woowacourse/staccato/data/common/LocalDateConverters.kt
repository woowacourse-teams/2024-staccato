package com.woowacourse.staccato.data.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val DATETIME_FORMAT_PATTERN = "yyyy-MM-dd"

fun String.toLocalDate(): LocalDate {
    return LocalDate.parse(this, DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN))
}
