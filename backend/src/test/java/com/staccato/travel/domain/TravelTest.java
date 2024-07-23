package com.staccato.travel.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.exception.StaccatoException;

class TravelTest {
    @DisplayName("끝 날짜는 시작 날짜보다 앞설 수 없다.")
    @Test
    void validateDate() {
        assertThatCode(() -> Travel.builder()
                .endAt(LocalDate.MIN)
                .startAt(LocalDate.MAX)
                .build())
                .isInstanceOf(StaccatoException.class)
                .hasMessage("끝 날짜가 시작 날짜보다 앞설 수 없어요.");
    }
}
