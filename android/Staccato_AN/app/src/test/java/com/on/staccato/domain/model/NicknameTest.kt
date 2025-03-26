package com.on.staccato.domain.model

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class NicknameTest {
    @ParameterizedTest
    @ValueSource(
        strings = ["스타카토", "Staccato", "stct0701", "star_cato", "스타카토 S2", "_st. .ato_"],
    )
    fun `닉네임은 한글, 영문, 숫자, 마침표, 밑줄, 띄어쓰기로 조합할 수 있다`(nickname: String) {
        // when
        val nicknameState = NicknameState.from(nickname)

        // then
        assertThat(nicknameState).isInstanceOf(NicknameState.Valid::class.java)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["ㅎㅎㅋㅋ", "ㅎrㅇi", "ㅏㅚㅙㅖ", "ㅇㅅㅇ2", "ㅅㅡㅌㅏㅋㅏㅌㅗ"],
    )
    fun `한글은 자음과 모음이 포함될 수 있다`(nickname: String) {
        // when
        val nicknameState = NicknameState.from(nickname)

        // then
        assertThat(nicknameState).isInstanceOf(NicknameState.Valid::class.java)
    }

    @Test
    fun `닉네임이 빈 문자열이라면 비어있는 상태이다`() {
        // when
        val nicknameState = NicknameState.from("")

        // then
        assertThat(nicknameState).isEqualTo(NicknameState.Empty)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [" ", "  ", "  nickname"],
    )
    fun `닉네임 첫 글자에 공백이 오면 유효하지 않은 형식이다`(blankFirstString: String) {
        // when
        val nicknameState = NicknameState.from(blankFirstString)

        // then
        assertThat(nicknameState).isEqualTo(NicknameState.BlankFirst)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["스타카토!", "st@cc@to", "star*cato", "수타카토~^^", "해피 :)"],
    )
    fun `닉네임에 밑줄, 마침표를 제외한 다른 특수기호가 포함되어 있다면 유효하지 않은 형식이다`(invalidNickname: String) {
        // when
        val nicknameState = NicknameState.from(invalidNickname)

        // then
        assertThat(nicknameState).isEqualTo(NicknameState.InvalidFormat)
    }

    @Test
    fun `닉네임의 길이는 1자 이상 10자 이내이며, 10자를 초과하면 유효하지 않은 길이이다`() {
        // when : 10자를 초과한 문자열 (테스트 문자열: 11자)
        val nicknameState = NicknameState.from("엄청 기이이인 닉네임")

        // then
        assertThat(nicknameState).isInstanceOf(NicknameState.InvalidLength::class.java)
    }
}
