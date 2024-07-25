package com.staccato.travel.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.exception.StaccatoException;
import com.staccato.pin.domain.Pin;
import com.staccato.visit.domain.Visit;

class TravelTest {
    @DisplayName("끝 날짜는 시작 날짜보다 앞설 수 없다.")
    @Test
    void validateDate() {
        assertThatCode(() -> Travel.builder()
                .title("2023 여름 여행")
                .endAt(LocalDate.MIN)
                .startAt(LocalDate.MAX)
                .build())
                .isInstanceOf(StaccatoException.class)
                .hasMessage("끝 날짜가 시작 날짜보다 앞설 수 없어요.");
    }

    @DisplayName("특정 날짜가 여행 상세 날짜에 속하는지 알 수 있다.")
    @Test
    void withinDuration() {
        // given
        Travel travel = Travel.builder()
                .title("2023 여름 여행")
                .startAt(LocalDate.of(2023, 7, 1))
                .endAt(LocalDate.of(2023, 7, 10))
                .build();

        // when & then
        assertThat(travel.withinDuration(LocalDate.of(2023, 7, 11))).isFalse();
    }

    @DisplayName("여행 상세를 수정 시 기존 방문 기록 날짜를 포함하지 않는 경우 수정에 실패한다.")
    @Test
    void validateDuration() {
        // given
        Travel travel = Travel.builder()
                .title("2023 여름 여행")
                .startAt(LocalDate.of(2023, 7, 1))
                .endAt(LocalDate.of(2023, 7, 10))
                .build();
        Travel updatedTravel = Travel.builder()
                .title("2023 여름 여행")
                .startAt(LocalDate.of(2023, 7, 20))
                .endAt(LocalDate.of(2023, 7, 21))
                .build();
        Pin pin = Pin.builder()
                .place("Sample Place")
                .address("Sample Address")
                .build();
        Visit visit = Visit.builder()
                .visitedAt(LocalDate.of(2023, 7, 1))
                .pin(pin)
                .travel(travel)
                .build();

        // when & then
        assertThatThrownBy(() -> travel.update(updatedTravel, List.of(visit)))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("변경하려는 여행 기간이 이미 존재하는 방문 기록을 포함하지 않습니다. 여행 기간을 다시 설정해주세요.");
    }
}
