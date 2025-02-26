package com.on.staccato.domain.model

@JvmInline
value class Nickname(val nickname: String) {
    companion object {
        private const val MIN_LENGTH = 1
        private const val MAX_LENGTH = 20
        private const val FORMAT_REGEX_PATTERN = "(?=.*[a-zA-Z가-힣._\\d])[a-zA-Z가-힣._\\d ]{1,20}"
        private val formatRegex = FORMAT_REGEX_PATTERN.toRegex()

        fun validate(nickname: String): NicknameState {
            return when {
                nickname.isBlank() -> NicknameState.Empty
                nickname.length !in MIN_LENGTH..MAX_LENGTH ->
                    NicknameState.InvalidLength(
                        MIN_LENGTH,
                        MAX_LENGTH,
                    )

                nickname.matches(formatRegex) -> NicknameState.Valid(nickname)
                else -> NicknameState.InvalidFormat
            }
        }
    }
}
