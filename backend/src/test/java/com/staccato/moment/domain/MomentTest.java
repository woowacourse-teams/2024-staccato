package com.staccato.moment.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.memory.domain.Memory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MomentTest {
    @DisplayName("추억 날짜 안에 스타카토 날짜가 포함되면 Moment을 생성할 수 있다.")
    @Test
    void createMoment() {
        // given
        Memory memory = Memory.builder()
                .title("Sample Memory")
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(1))
                .build();

        // when & then
        assertThatCode(() -> Moment.builder()
                .visitedAt(LocalDateTime.now().plusDays(1))
                .title("staccatoTitle")
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .memory(memory)
                .build()).doesNotThrowAnyException();
    }

    @DisplayName("추억 기간이 없는 경우 스타카토를 날짜 상관없이 생성할 수 있다.")
    @Test
    void createMomentInUndefinedDuration() {
        // given
        Memory memory = Memory.builder()
                .title("Sample Memory")
                .build();

        // when & then
        assertThatCode(() -> Moment.builder()
                .visitedAt(LocalDateTime.now().plusDays(1))
                .title("staccatoTitle")
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .memory(memory)
                .build()).doesNotThrowAnyException();
    }

    @DisplayName("스타카토 생성 시 title의 앞 뒤 공백이 제거된다.")
    @Test
    void trimPlaceName() {
        // given
        Memory memory = MemoryFixture.create(MemberFixture.create());
        String title = " staccatoTitle ";
        String expectedTitle = "staccatoTitle";

        // when
        Moment moment = Moment.builder()
                .visitedAt(LocalDateTime.of(memory.getTerm().getStartAt(), LocalTime.MIN))
                .title(title)
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .placeName("placeName")
                .address("address")
                .memory(memory)
                .build();

        // then
        assertThat(moment.getTitle()).isEqualTo(expectedTitle);
    }

    @DisplayName("추억 날짜 안에 스타카토 날짜가 포함되지 않으면 예외를 발생시킨다.")
    @ValueSource(longs = {-1, 2})
    @ParameterizedTest
    void failCreateMoment(long plusDays) {
        // given
        Memory memory = Memory.builder()
                .title("Sample Memory")
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(1))
                .build();

        // when & then
        assertThatThrownBy(() -> Moment.builder()
                .visitedAt(LocalDateTime.now().plusDays(plusDays))
                .title("staccatoTitle")
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .memory(memory)
                .build()).isInstanceOf(StaccatoException.class)
                .hasMessageContaining("추억에 포함되지 않는 날짜입니다.");
    }
}
