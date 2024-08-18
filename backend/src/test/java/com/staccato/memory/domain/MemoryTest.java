package com.staccato.memory.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.exception.StaccatoException;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.moment.domain.Moment;

class MemoryTest {
    @DisplayName("추억을 수정 시 기존 순간 기록 날짜를 포함하지 않는 경우 수정에 실패한다.")
    @Test
    void validateDuration() {
        // given
        Memory memory = MemoryFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));
        Memory updatedMemory = MemoryFixture.create(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        Moment moment = MomentFixture.create(memory, LocalDateTime.now());

        // when & then
        assertThatThrownBy(() -> memory.update(updatedMemory, List.of(moment)))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("변경하려는 추억 기간이 이미 존재하는 순간을 포함하지 않습니다. 추억 기간을 다시 설정해주세요.");
    }
}
