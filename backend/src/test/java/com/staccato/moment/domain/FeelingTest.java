package com.staccato.moment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.staccato.exception.StaccatoException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeelingTest {

    @DisplayName("기분을 선택할 수 있다.")
    @Test
    void match() {
        assertThat(Feeling.match("happy")).isEqualTo(Feeling.HAPPY);
    }

    @DisplayName("존재하지 않는 기분을 선택할 경우 예외가 발생한다.")
    @Test
    void failMatch() {
        assertThatThrownBy(() -> Feeling.match("upset"))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 기분 표현을 찾을 수 없어요.");
    }
}
