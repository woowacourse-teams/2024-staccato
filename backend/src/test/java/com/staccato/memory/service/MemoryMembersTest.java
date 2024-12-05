package com.staccato.memory.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.repository.MemoryMemberRepository;
import com.staccato.memory.repository.MemoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemoryMembersTest extends ServiceSliceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private MemoryMemberRepository memoryMemberRepository;

    @DisplayName("MemoryMember 목록을 생성 시간 기준 내림차순으로 조회된다.")
    @Test
    void readAllMemories() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        memoryRepository.save(MemoryFixture.createWithMember("first", member));
        memoryRepository.save(MemoryFixture.createWithMember("second", member));

        MemoryMembers memoryMembers = new MemoryMembers(memoryMemberRepository.findAllByMemberId(member.getId()));

        // when
        memoryMembers.descendByCreatedDate();

        // then
        assertAll(
                () -> assertThat(memoryMembers.getMemories().get(0).getTitle()).isEqualTo("second"),
                () -> assertThat(memoryMembers.getMemories().get(1).getTitle()).isEqualTo("first")
        );
    }
}
