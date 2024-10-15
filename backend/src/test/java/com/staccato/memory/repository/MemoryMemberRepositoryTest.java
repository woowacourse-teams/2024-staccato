package com.staccato.memory.repository;

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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemoryMemberRepositoryTest {
    @Autowired
    private MemoryMemberRepository memoryMemberRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemoryRepository memoryRepository;

    @DisplayName("사용자의 모든 추억 목록을 조회한다.")
    @Test
    void findAllByMemberId() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Memory memory = memoryRepository.save(MemoryFixture.create(LocalDate.of(2023, 12, 30), LocalDate.of(2023, 12, 30)));
        Memory memory2 = memoryRepository.save(MemoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2023, 12, 31)));
        memoryMemberRepository.save(new MemoryMember(member, memory));
        memoryMemberRepository.save(new MemoryMember(member, memory2));

        // when
        List<MemoryMember> result = memoryMemberRepository.findAllByMemberId(member.getId());

        // then
        assertThat(result).hasSize(2);
    }

    @DisplayName("사용자 식별자와 날짜로 추억 목록을 조회한다.")
    @Test
    void findAllByMemberIdAndDate() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Memory memory = memoryRepository.save(MemoryFixture.create(LocalDate.of(2023, 12, 30), LocalDate.of(2023, 12, 30)));
        Memory memory2 = memoryRepository.save(MemoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2023, 12, 31)));
        memoryMemberRepository.save(new MemoryMember(member, memory));
        memoryMemberRepository.save(new MemoryMember(member, memory2));

        // when
        List<MemoryMember> result = memoryMemberRepository.findAllByMemberIdAndIncludingDate(member.getId(), LocalDate.of(2023, 12, 31));

        // then
        assertThat(result).hasSize(1);
    }

    @DisplayName("사용자 식별자와 날짜로 추억 목록을 조회할 때 추억에 기한이 없을 경우 함께 조회한다.")
    @Test
    void findAllByMemberIdAndDateWhenNull() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Memory memory = memoryRepository.save(MemoryFixture.create(LocalDate.of(2023, 12, 30), LocalDate.of(2023, 12, 30)));
        Memory memory2 = memoryRepository.save(MemoryFixture.create(null, null));
        memoryMemberRepository.save(new MemoryMember(member, memory));
        memoryMemberRepository.save(new MemoryMember(member, memory2));

        // when
        List<MemoryMember> result = memoryMemberRepository.findAllByMemberIdAndIncludingDate(member.getId(), LocalDate.of(2023, 12, 30));

        // then
        assertThat(result).hasSize(2);
    }
}
