package com.staccato.category.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.staccato.exception.StaccatoException;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescriptionTest {
    private static final int MAX_LENGTH = 500;

    @DisplayName("카테고리 내용은 500자 이하로 생성할 수 있다.")
    @Test
    void createDescriptionLengthInRange() {
        // given
        String nickname = "가".repeat(MAX_LENGTH);

        // when & then
        assertThatNoException().isThrownBy(() -> new Description(nickname));
    }

    @DisplayName("카테고리 내용은 500자 초과로 생성할 수 없다.")
    @Test
    void cannotCreateNicknameLengthOutOfRange() {
        // given
        String nickname = "가".repeat(MAX_LENGTH + 1);

        // when & then
        assertThatThrownBy(() -> new Description(nickname))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("내용의 최대 허용 글자수는 공백 포함 500자입니다.");
    }
}
