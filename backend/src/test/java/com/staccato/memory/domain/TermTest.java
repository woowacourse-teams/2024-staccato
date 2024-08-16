package com.staccato.memory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.exception.StaccatoException;

class TermTest {
    @DisplayName("끝 날짜는 시작 날짜보다 앞설 수 없다.")
    @Test
    void validateDate() {
        assertThatCode(() -> new Term(LocalDate.now().plusDays(1), LocalDate.now()))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("끝 날짜가 시작 날짜보다 앞설 수 없어요.");
    }

    @DisplayName("특정 날짜가 추억 날짜에 속하는지 알 수 있다.")
    @Test
    void withoutDuration() {
        // given
        Term term = new Term(LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10));

        // when & then
        assertThat(term.doesNotContain(LocalDateTime.of(2023, 7, 11, 10, 0))).isTrue();
    }
}
