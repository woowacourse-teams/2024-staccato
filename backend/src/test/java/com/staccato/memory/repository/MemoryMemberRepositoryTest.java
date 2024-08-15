package com.staccato.memory.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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

    @DisplayName("사용자 식별자와 년도로 추억 상세 목록을 조회한다.")
    @Test
    void findAllByMemberIdAndMemoryStartAtYear() {
        // given
        Member member = memberRepository.save(Member.builder().nickname("staccato").build());
        Memory memory = memoryRepository.save(createMemory(LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)));
        Memory memory2 = memoryRepository.save(createMemory(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Memory memory3 = memoryRepository.save(createMemory(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10)));
        memoryMemberRepository.save(new MemoryMember(member, memory));
        memoryMemberRepository.save(new MemoryMember(member, memory2));
        memoryMemberRepository.save(new MemoryMember(member, memory3));

        // when
        List<MemoryMember> result = memoryMemberRepository.findAllByMemberIdAndStartAtYearDesc(member.getId(), 2023);

        // then
        assertThat(result).hasSize(2);
    }

    @DisplayName("사용자 식별자와 년도로 해당하는 삭제되지 않은 추억 상세 목록만 최신순으로 조회한다.")
    @Test
    void findAllByMemberIdAndMemoryStartAtYearWithoutDeleted() {
        // given
        Member member = memberRepository.save(Member.builder().nickname("staccato").build());
        Memory memory = memoryRepository.save(createMemory(LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)));
        Memory memory2 = memoryRepository.save(createMemory(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Memory memory3 = memoryRepository.save(createMemory(LocalDate.of(2023, 1, 10), LocalDate.of(2024, 1, 10)));
        memoryMemberRepository.save(new MemoryMember(member, memory));
        memoryMemberRepository.save(new MemoryMember(member, memory2));
        memoryMemberRepository.save(new MemoryMember(member, memory3));
        memoryRepository.deleteById(memory3.getId());

        // when
        List<MemoryMember> result = memoryMemberRepository.findAllByMemberIdAndStartAtYearDesc(member.getId(), 2023);

        // then
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.stream().map(MemoryMember::getMemory).map(Memory::getStartAt)
                        .toList()).containsExactly(LocalDate.of(2023, 12, 31), LocalDate.of(2023, 7, 1))

        );
    }

    private static Memory createMemory(LocalDate startAt, LocalDate endAt) {
        return Memory.builder()
                .title("추억")
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }
}
