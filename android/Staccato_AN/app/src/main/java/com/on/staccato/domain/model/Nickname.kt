package com.on.staccato.domain.model

@JvmInline
value class Nickname(val value: String) {
    fun isEmpty(): Boolean = value.isEmpty()

    fun isBlankFirst(): Boolean = value.first().isWhitespace()

    fun isValidLength(): Boolean = value.length in MIN_LENGTH..MAX_LENGTH

    fun isValidFormat(): Boolean = value.matches(formatRegex)

    companion object {
        const val MIN_LENGTH = 1
        const val MAX_LENGTH = 10
        private const val FORMAT_REGEX_PATTERN = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z._\\d ]+$"
        private val formatRegex = FORMAT_REGEX_PATTERN.toRegex()
    }
}
