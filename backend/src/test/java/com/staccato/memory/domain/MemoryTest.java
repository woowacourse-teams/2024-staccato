package com.staccato.memory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.domain.MomentImages;

class MemoryTest {
    @DisplayName("끝 날짜는 시작 날짜보다 앞설 수 없다.")
    @Test
    void validateDate() {
        assertThatCode(() -> Memory.builder()
                .title("2023 여름 추억")
                .endAt(LocalDate.MIN)
                .startAt(LocalDate.MAX)
                .build())
                .isInstanceOf(StaccatoException.class)
                .hasMessage("끝 날짜가 시작 날짜보다 앞설 수 없어요.");
    }

    @DisplayName("특정 날짜가 추억 상세 날짜에 속하는지 알 수 있다.")
    @Test
    void withoutDuration() {
        // given
        Memory memory = Memory.builder()
                .title("2023 여름 추억")
                .startAt(LocalDate.of(2023, 7, 1))
                .endAt(LocalDate.of(2023, 7, 10))
                .build();

        // when & then
        assertThat(memory.isWithoutDuration(LocalDateTime.of(2023, 7, 11, 10, 0))).isTrue();
    }

    @DisplayName("추억 상세를 수정 시 기존 순간 기록 날짜를 포함하지 않는 경우 수정에 실패한다.")
    @Test
    void validateDuration() {
        // given
        Memory memory = Memory.builder()
                .title("2023 여름 추억")
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(1))
                .build();
        Memory updatedMemory = Memory.builder()
                .title("2023 여름 추억")
                .startAt(LocalDate.now().plusDays(1))
                .endAt(LocalDate.now().plusDays(2))
                .build();
        Member member = Member.builder()
                .nickname("staccato")
                .build();
        Moment moment = Moment.builder()
                .visitedAt(LocalDateTime.now())
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .memory(memory)
                .momentImages(new MomentImages(List.of()))
                .build();

        // when & then
        assertThatThrownBy(() -> memory.update(updatedMemory, List.of(moment)))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("변경하려는 추억 기간이 이미 존재하는 순간 기록을 포함하지 않습니다. 추억 기간을 다시 설정해주세요.");
    }
}
