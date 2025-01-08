package com.staccato.memory.service;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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

public class MemorySortTest extends ServiceSliceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemoryRepository memoryRepository;

    @DisplayName("조건에 맞지 않는 값이나 유효하지 않는 값이 들어올 경우 기본 정렬(최근 수정 순)로 추억 목록을 조회된다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "invalid")
    void readAllMemoriesIfInvalidCondition(String name) {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        List<Memory> memories = new ArrayList<>();
        memories.add(memoryRepository.save(MemoryFixture.createWithMember("first", member)));
        memories.add(memoryRepository.save(MemoryFixture.createWithMember("second", member)));

        // when
        List<Memory> result = MemorySort.apply(name, memories);

        // then
        assertAll(
                () -> assertThat(result.get(0).getTitle()).isEqualTo("second"),
                () -> assertThat(result.get(1).getTitle()).isEqualTo("first")
        );
    }

    @DisplayName("조건 판별 시 대소문자 관계 없이 추억 목록을 조회된다.")
    @Test
    void ignoreCaseOfSortName() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        List<Memory> memories = new ArrayList<>();
        memories.add(memoryRepository.save(MemoryFixture.createWithMember("first", member)));
        memories.add(memoryRepository.save(MemoryFixture.createWithMember("second", member)));

        // when
        List<Memory> result = MemorySort.apply("oldest", memories);

        // then
        assertAll(
                () -> assertThat(result.get(0).getTitle()).isEqualTo("first"),
                () -> assertThat(result.get(1).getTitle()).isEqualTo("second")
        );
    }

    @DisplayName("수정 시간 기준 내림차순으로 추억 목록을 조회된다.")
    @Test
    void readAllMemoriesByUpdatedAtDesc() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        List<Memory> memories = new ArrayList<>();
        memories.add(memoryRepository.save(MemoryFixture.createWithMember("first", member)));
        memories.add(memoryRepository.save(MemoryFixture.createWithMember("second", member)));

        // when
        List<Memory> result = MemorySort.apply("UPDATED", memories);

        // then
        assertAll(
                () -> assertThat(result.get(0).getTitle()).isEqualTo("second"),
                () -> assertThat(result.get(1).getTitle()).isEqualTo("first")
        );
    }

    @DisplayName("최근 생성순으로 추억 목록을 조회된다.")
    @Test
    void readAllMemoriesByCreatedAtDesc() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        List<Memory> memories = new ArrayList<>();
        memories.add(memoryRepository.save(MemoryFixture.createWithMember("first", member)));
        memories.add(memoryRepository.save(MemoryFixture.createWithMember("second", member)));

        // when
        List<Memory> result = MemorySort.apply("NEWEST", memories);

        // then
        assertAll(
                () -> assertThat(result.get(0).getTitle()).isEqualTo("second"),
                () -> assertThat(result.get(1).getTitle()).isEqualTo("first")
        );
    }

    @DisplayName("오래된 순으로 추억 목록을 조회된다.")
    @Test
    void readAllMemoriesByCreatedAtAsc() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        List<Memory> memories = new ArrayList<>();
        memories.add(memoryRepository.save(MemoryFixture.createWithMember("first", member)));
        memories.add(memoryRepository.save(MemoryFixture.createWithMember("second", member)));

        // when
        List<Memory> result = MemorySort.apply("OLDEST", memories);

        // then
        assertAll(
                () -> assertThat(result.get(0).getTitle()).isEqualTo("first"),
                () -> assertThat(result.get(1).getTitle()).isEqualTo("second")
        );
    }
}
