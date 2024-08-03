package com.staccato.visit.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.staccato.exception.StaccatoException;
import com.staccato.travel.domain.Travel;

class VisitTest {

    @DisplayName("여행 날짜 안에 방문 기록 날짜가 포함되면 Visit을 생성할 수 있다.")
    @Test
    void createVisit() {
        // given
        Travel travel = Travel.builder()
                .title("Sample Travel")
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(1))
                .build();

        // when & then
        assertThatCode(() -> Visit.builder()
                .visitedAt(LocalDate.now().plusDays(1))
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .travel(travel)
                .build()).doesNotThrowAnyException();
    }

    @DisplayName("여행 날짜 안에 방문 기록 날짜가 포함되지 않으면 예외를 발생시킨다.")
    @ValueSource(longs = {-1, 2})
    @ParameterizedTest
    void failCreateVisit(long plusDays) {
        // given
        Travel travel = Travel.builder()
                .title("Sample Travel")
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(1))
                .build();

        // when & then
        assertThatThrownBy(() -> Visit.builder()
                .visitedAt(LocalDate.now().plusDays(plusDays))
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .travel(travel)
                .build()).isInstanceOf(StaccatoException.class)
                .hasMessageContaining("여행에 포함되지 않는 날짜입니다.");
    }
}
