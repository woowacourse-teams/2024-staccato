package com.staccato.memory.service;

import java.time.LocalDate;
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

class MemoriesTest extends ServiceSliceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private MemoryMemberRepository memoryMemberRepository;

    @DisplayName("주어진 조건을 적용하여 추억 목록을 반환한다.")
    @Test
    void readAllMemoriesWithTerm() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Memory memory = memoryRepository.save(MemoryFixture.createWithMember("first", member));
        Memory memory2 = memoryRepository.save(MemoryFixture.createWithMember(LocalDate.now(), LocalDate.now()
                .plusDays(3), member));

        Memories memories = Memories.from(memoryMemberRepository.findAllByMemberId(member.getId()));

        // when
        List<Memory> result = memories.operate(List.of(), MemorySort.OLDEST);

        // then
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.get(0).getTitle()).isEqualTo(memory.getTitle()),
                () -> assertThat(result.get(1).getTitle()).isEqualTo(memory2.getTitle())
        );
    }
}
