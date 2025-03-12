package com.on.staccato.domain.model

@JvmInline
value class Nickname(val nickname: String) {
    companion object {
        const val MAX_LENGTH = 10
        private const val MIN_LENGTH = 1
        private const val FORMAT_REGEX_PATTERN = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z._\\d ]+$"
        private val formatRegex = FORMAT_REGEX_PATTERN.toRegex()

        fun validate(nickname: String): NicknameState {
            return when {
                nickname.isEmpty() -> NicknameState.Empty

                nickname.length !in MIN_LENGTH..MAX_LENGTH ->
                    NicknameState.InvalidLength(
                        MIN_LENGTH,
                        MAX_LENGTH,
                    )

                nickname.first().isWhitespace() -> NicknameState.BlankFirst

                nickname.matches(formatRegex) -> NicknameState.Valid(nickname)

                else -> NicknameState.InvalidFormat
            }
        }
    }
}
