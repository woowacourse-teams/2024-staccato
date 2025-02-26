package com.on.staccato.domain.model

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

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

sealed interface NicknameState {
    data object Empty : NicknameState

    data class Valid(val nickname: String) : NicknameState

    data object InvalidFormat : NicknameState

    data class InvalidLength(
        val min: Int,
        val max: Int,
    ) : NicknameState
}

class NicknameTest {
    @ParameterizedTest
    @ValueSource(
        strings = ["스타카토", "Staccato", "staccato0701", "star_cato", "스타카토 S2", "_sta .c .cato_"],
    )
    fun `닉네임은 한글, 영문, 숫자, 마침표, 밑줄, 띄어쓰기로 조합할 수 있다`(nickname: String) {
        // when
        val nicknameState = Nickname.validate(nickname)

        // then
        assertThat(nicknameState).isInstanceOf(NicknameState.Valid::class.java)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["", " ", "     "],
    )
    fun `닉네임이 공백으로만 이루어져 있다면 비어있는 상태이다`(blankString: String) {
        // when
        val nicknameState = Nickname.validate(blankString)

        // then
        assertThat(nicknameState).isEqualTo(NicknameState.Empty)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["스타카토!", "st@cc@to", "star-*-cato", "수타카토~^^", "해피 :)"],
    )
    fun `닉네임에 밑줄, 마침표를 제외한 다른 특수기호가 포함되어 있다면 올바르지 않은 형식이다`(nickname: String) {
        // when
        val nicknameState = Nickname.validate(nickname)

        // then
        assertThat(nicknameState).isEqualTo(NicknameState.InvalidFormat)
    }

    @Test
    fun `닉네임의 길이는 1자 이상 20자 이내이며, 20자를 초과하면 올바르지 않은 길이이다`() {
        // when : 20자를 초과한 문자열(현재 문자열 = 21자)
        val nicknameState = Nickname.validate("무지무지하게 기이이이이이이이이인 닉네임")

        // then
        assertThat(nicknameState).isInstanceOf(NicknameState.InvalidLength::class.java)
    }
}
