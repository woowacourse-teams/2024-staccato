package com.on.staccato.domain.model

@JvmInline
value class Nickname(val value: String) {
    fun validate(): NicknameState {
        return when {
            value.isEmpty() -> NicknameState.Empty

            value.first().isWhitespace() -> NicknameState.BlankFirst

            value.matches(formatRegex).not() -> NicknameState.InvalidFormat

            value.length !in MIN_LENGTH..MAX_LENGTH ->
                NicknameState.InvalidLength(
                    MIN_LENGTH,
                    MAX_LENGTH,
                )

            else -> NicknameState.Valid(value)
        }
    }

    companion object {
        const val MAX_LENGTH = 10
        private const val MIN_LENGTH = 1
        private const val FORMAT_REGEX_PATTERN = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z._\\d ]+$"
        private val formatRegex = FORMAT_REGEX_PATTERN.toRegex()
    }
}
