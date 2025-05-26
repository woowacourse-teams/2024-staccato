package com.staccato.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.staccato.exception.StaccatoException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NicknameTest {
    @DisplayName("유효한 닉네임을 생성한다.")
    @Test
    void createNickname() {
        assertThatNoException().isThrownBy(() -> new Nickname("가ㄱㅏ.AZ1az_"));
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
    @ValueSource(strings = {"|", "//", "+"})
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

    @DisplayName("닉네임은 1자 이상, 10자 이하로 생성할 수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    void createNicknameLengthInRange(int count) {
        // given
        String nickname = "가".repeat(count);

        // when & then
        assertThatNoException().isThrownBy(() -> new Nickname(nickname));
    }

    @DisplayName("닉네임은 1자 미만, 10자 초과로 생성할 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    void cannotCreateNicknameLengthOutOfRange(int count) {
        // given
        String nickname = "가".repeat(count);

        // when & then
        assertThatThrownBy(() -> new Nickname(nickname))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("1자 이상 10자 이하의 닉네임으로 설정해주세요.");
    }
}
