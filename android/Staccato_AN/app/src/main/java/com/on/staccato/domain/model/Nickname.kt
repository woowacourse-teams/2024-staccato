package com.on.staccato.domain.model

@JvmInline
value class Nickname(val value: String) {
    fun validate(): NicknameState {
        return when {
            value.isEmpty() -> NicknameState.Empty

            value.length !in MIN_LENGTH..MAX_LENGTH ->
                NicknameState.InvalidLength(
                    MIN_LENGTH,
                    MAX_LENGTH,
                )

            value.first().isWhitespace() -> NicknameState.BlankFirst

            value.matches(formatRegex) -> NicknameState.Valid(value)

            else -> NicknameState.InvalidFormat
        }
    }

    companion object {
        const val MAX_LENGTH = 10
        private const val MIN_LENGTH = 1
        private const val FORMAT_REGEX_PATTERN = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z._\\d ]+$"
        private val formatRegex = FORMAT_REGEX_PATTERN.toRegex()
    }
}
