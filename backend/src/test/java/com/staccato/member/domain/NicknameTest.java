package com.staccato.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.staccato.exception.StaccatoException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NicknameTest {
    @DisplayName("유효한 닉네임을 생성한다.")
    @Test
    void createNickname() {
        assertThatNoException().isThrownBy(() -> new Nickname("가ㄱㅏㅣㅎ.AZ1az_"));
    }

    @DisplayName("닉네임 중간에 공백을 포함할 수 있다.")
    @Test
    void createNicknameWithBlank() {
        assertThatNoException().isThrownBy(() -> new Nickname("가  가"));
    }

    @DisplayName("닉네임에는 한글 자음/모음을 포함할 수 있다.")
    @Test
    void createNicknameWithIncompleteKorean() {
        assertThatNoException().isThrownBy(() -> new Nickname("ㅎrㅇi"));
    }

    @DisplayName("닉네임의 형식이 잘못되었을 경우 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {"  ", "//", ""})
    void cannotCreateNicknameByInvalidFormat(String name) {
        assertThatThrownBy(() -> new Nickname(name))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("올바르지 않은 닉네임 형식입니다.");
    }

    @DisplayName("닉네임 맨 앞, 뒤 공백은 제거된다.")
    @Test
    void createNicknameAfterTrim() {
        // given
        Nickname nickname = new Nickname(" staccato ");

        // when & then
        assertThat(nickname.getNickname()).isEqualTo("staccato");
    }
}
