package com.staccato.member.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.staccato.exception.StaccatoException;

class NicknameTest {
    @DisplayName("유효한닉네임을 생성한다.")
    @Test
    void CreateNickname() {
        assertThatNoException().isThrownBy(() -> new Nickname("가ㄱㅏㅣㅎ.AZaz_"));
    }

    @DisplayName("닉네임의 길이가 잘못되었을 경우 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 21})
    void cannotCreateNicknameByInvalidLength(int length) {
        assertThatThrownBy(() -> new Nickname("가".repeat(length)))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("20자 이내의 닉네임으로 설정해주세요.");
    }

    @DisplayName("닉네임의 형식이 잘못되었을 경우 예외를 발생시킨다.")
    @Test
    void cannotCreateNicknameByInvalidFormat() {
        assertThatThrownBy(() -> new Nickname("//"))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("올바르지 않은 닉네임 형식입니다.");
    }
}
