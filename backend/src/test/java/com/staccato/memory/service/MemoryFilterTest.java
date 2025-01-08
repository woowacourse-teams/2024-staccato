package com.staccato.memory.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemoryFilterTest extends ServiceSliceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemoryRepository memoryRepository;

    @DisplayName("필터링 조건이 없으면 추억 목록을 그대로 반환한다.")
    @Test
    void readAllMemoriesIfNoCondition() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Memory memory = memoryRepository.save(MemoryFixture.createWithMember("first", member));
        Memory memory2 = memoryRepository.save(MemoryFixture.createWithMember(LocalDate.now(), LocalDate.now()
                .plusDays(3), member));
        List<Memory> memories = new ArrayList<>();
        memories.add(memory);
        memories.add(memory2);

        // when
        List<Memory> result = MemoryFilter.apply(List.of(), memories);

        // then
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.get(0).getTitle()).isEqualTo(memory.getTitle()),
                () -> assertThat(result.get(1).getTitle()).isEqualTo(memory2.getTitle())
        );
    }

    @DisplayName("유효하지 않은 필터링 조건은 적용되지 않고 추억 목록을 그대로 반환한다.")
    @Test
    void readAllMemoriesIfInvalidCondition() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Memory memory = memoryRepository.save(MemoryFixture.createWithMember("first", member));
        Memory memory2 = memoryRepository.save(MemoryFixture.createWithMember(LocalDate.now(), LocalDate.now()
                .plusDays(3), member));
        List<Memory> memories = new ArrayList<>();
        memories.add(memory);
        memories.add(memory2);

        // when
        List<Memory> result = MemoryFilter.apply(List.of("invalid"), memories);

        // then
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.get(0).getTitle()).isEqualTo(memory.getTitle()),
                () -> assertThat(result.get(1).getTitle()).isEqualTo(memory2.getTitle())
        );
    }

    @DisplayName("기간이 있는 추억 목록만 조회된다.")
    @Test
    void readAllMemoriesWithTerm() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Memory memory = memoryRepository.save(MemoryFixture.createWithMember("first", member));
        Memory memory2 = memoryRepository.save(MemoryFixture.createWithMember(LocalDate.now(), LocalDate.now()
                .plusDays(3), member));
        List<Memory> memories = new ArrayList<>();
        memories.add(memory);
        memories.add(memory2);

        // when
        List<Memory> result = MemoryFilter.apply(List.of("term"), memories);

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).getTitle()).isEqualTo(memory2.getTitle())
        );
    }
}
