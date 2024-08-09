package com.staccato.member.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.exception.StaccatoException;

class NicknameTest {
    @DisplayName("유효한 닉네임을 생성한다.")
    @Test
    void CreateNickname() {
        assertThatNoException().isThrownBy(() -> new Nickname("가ㄱㅏㅣㅎ.AZ1az_"));
    }

    @DisplayName("닉네임의 형식이 잘못되었을 경우 예외를 발생시킨다.")
    @Test
    void cannotCreateNicknameByInvalidFormat() {
        assertThatThrownBy(() -> new Nickname("//"))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("올바르지 않은 닉네임 형식입니다.");
    }
}
