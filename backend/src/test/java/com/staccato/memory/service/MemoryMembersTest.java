package com.staccato.memory.service;

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
import com.staccato.memory.repository.MemoryMemberRepository;
import com.staccato.memory.repository.MemoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemoryMembersTest extends ServiceSliceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private MemoryMemberRepository memoryMemberRepository;

    @DisplayName("MemoryMember 목록을 수정 시간 기준 내림차순으로 조회된다.")
    @Test
    void readAllMemoriesByUpdatedAtDesc() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        memoryRepository.save(MemoryFixture.createWithMember("first", member));
        memoryRepository.save(MemoryFixture.createWithMember("second", member));

        MemoryMembers memoryMembers = new MemoryMembers(memoryMemberRepository.findAllByMemberId(member.getId()));

        // when
        List<Memory> result = memoryMembers.orderMemoryByRecentlyUpdated();

        // then
        assertAll(
                () -> assertThat(result.get(0).getTitle()).isEqualTo("second"),
                () -> assertThat(result.get(1).getTitle()).isEqualTo("first")
        );
    }

    @DisplayName("MemoryMember 목록을 생성 시간 기준 내림차순으로 조회된다.")
    @Test
    void readAllMemoriesByCreatedAtDesc() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        memoryRepository.save(MemoryFixture.createWithMember("first", member));
        memoryRepository.save(MemoryFixture.createWithMember("second", member));

        MemoryMembers memoryMembers = new MemoryMembers(memoryMemberRepository.findAllByMemberId(member.getId()));

        // when
        List<Memory> result = memoryMembers.orderMemoryByNewest();

        // then
        assertAll(
                () -> assertThat(result.get(0).getTitle()).isEqualTo("second"),
                () -> assertThat(result.get(1).getTitle()).isEqualTo("first")
        );
    }

    @DisplayName("MemoryMember 목록을 생성 시간 기준 오름차순으로 조회된다.")
    @Test
    void readAllMemoriesByCreatedAtAsc() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        memoryRepository.save(MemoryFixture.createWithMember("first", member));
        memoryRepository.save(MemoryFixture.createWithMember("second", member));

        MemoryMembers memoryMembers = new MemoryMembers(memoryMemberRepository.findAllByMemberId(member.getId()));

        // when
        List<Memory> result = memoryMembers.orderMemoryByOldest();

        // then
        assertAll(
                () -> assertThat(result.get(0).getTitle()).isEqualTo("first"),
                () -> assertThat(result.get(1).getTitle()).isEqualTo("second")
        );
    }
}
