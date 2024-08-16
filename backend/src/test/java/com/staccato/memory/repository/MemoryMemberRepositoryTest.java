package com.staccato.memory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.domain.MemoryMember;

@DataJpaTest
class MemoryMemberRepositoryTest {
    @Autowired
    private MemoryMemberRepository memoryMemberRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemoryRepository memoryRepository;

    @DisplayName("사용자 식별자와 년도로 추억 목록을 조회한다.")
    @Test
    void findAllByMemberIdAndMemoryStartAtYear() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Memory memory = memoryRepository.save(MemoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Memory memory2 = memoryRepository.save(MemoryFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10)));
        memoryMemberRepository.save(new MemoryMember(member, memory));
        memoryMemberRepository.save(new MemoryMember(member, memory2));

        // when
        List<MemoryMember> result = memoryMemberRepository.findAllByMemberIdAndYearOrderByCreatedAtDesc(member.getId(), 2023);

        // then
        assertThat(result).hasSize(1);
    }
}
