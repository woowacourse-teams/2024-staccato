package com.staccato.memory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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

    @DisplayName("특정 날짜가 추억 기간에 속하지 않으면 참을 반환한다.")
    @Test
    void isOutOfTerm() {
        // given
        Term term = new Term(LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10));

        // when & then
        assertThat(term.doesNotContain(LocalDateTime.of(2023, 7, 11, 10, 0))).isTrue();
    }

    @DisplayName("특정 날짜가 추억 기간에 속하면 거짓을 반환한다.")
    @Test
    void isInTerm() {
        // given
        Term term = new Term(LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10));

        // when & then
        assertThat(term.doesNotContain(LocalDateTime.of(2023, 7, 1, 10, 0))).isFalse();
    }

    @DisplayName("추억 기간이 없다면, 어떤 날짜든 거짓을 반환한다.")
    @Test
    void isNoTerm() {
        // given
        Term term = new Term(null, null);

        // when & then
        assertThat(term.doesNotContain(LocalDateTime.of(2023, 7, 11, 10, 0))).isFalse();
    }

    @DisplayName("끝 날짜는 있는데, 시작 날짜가 누락되면 예외를 발생한다.")
    @Test
    void cannotCreateTermByNoStartAt() {
        assertThatThrownBy(() -> new Term(null, LocalDate.now()))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("추억 시작 날짜와 끝 날짜를 모두 입력해주세요.");
    }

    @DisplayName("시작 날짜는 있는데, 끝 날짜가 누락되면 예외를 발생한다.")
    @Test
    void cannotCreateTermByNoEndAt() {
        assertThatThrownBy(() -> new Term(LocalDate.now(), null))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("추억 시작 날짜와 끝 날짜를 모두 입력해주세요.");
    }
}
