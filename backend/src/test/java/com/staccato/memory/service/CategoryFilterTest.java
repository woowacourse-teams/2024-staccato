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

class CategoryFilterTest extends ServiceSliceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemoryRepository memoryRepository;

    @DisplayName("필터링명이 주어졌을 때 대소문자 구분 없이 MemoryFilter을 반환한다.")
    @Test
    void findByNameWithValidFilter() {
        // when
        List<CategoryFilter> result = CategoryFilter.findAllByName(List.of("term"));

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0)).isEqualTo(CategoryFilter.TERM)
        );
    }

    @DisplayName("필터링명이 주어졌을 때 유효한 MemoryFilter 목록만 반환한다.")
    @Test
    void findAllByNameIfOnlyValid() {
        // when
        List<CategoryFilter> result = CategoryFilter.findAllByName(List.of("invalid", "term"));

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0)).isEqualTo(CategoryFilter.TERM)
        );
    }

    @DisplayName("필터링명이 중복으로 주어졌을 때 중복 없이 MemoryFilter 목록을 반환한다.")
    @Test
    void findAllByNameDistinct() {
        // when
        List<CategoryFilter> result = CategoryFilter.findAllByName(List.of("TERM", "term"));

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0)).isEqualTo(CategoryFilter.TERM)
        );
    }

    @DisplayName("유효하지 않거나 null인 정렬명이 주어졌을 때 빈 값을 반환한다.")
    @Test
    void findByNameWithInvalid() {
        // when
        List<CategoryFilter> result = CategoryFilter.findAllByName(List.of("invalid"));

        // then
        assertThat(result).isEmpty();
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
        List<Memory> result = CategoryFilter.TERM.apply(memories);

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).getTitle()).isEqualTo(memory2.getTitle())
        );
    }
}
