package com.staccato.moment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.domain.MemoryMember;
import com.staccato.memory.repository.MemoryMemberRepository;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.domain.Moment;

@DataJpaTest
class MomentRepositoryTest {
    @Autowired
    private MomentRepository momentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private MemoryMemberRepository memoryMemberRepository;

    @DisplayName("사용자의 모든 스타카토를 조회한다.")
    @Test
    void findAllByMemory_MemoryMembers_Member() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Member anotherMember = memberRepository.save(MemberFixture.create("anotherMember"));
        Memory memory = memoryRepository.save(MemoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Memory memory2 = memoryRepository.save(MemoryFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10)));
        Memory anotherMemberMemory = memoryRepository.save(MemoryFixture.create(LocalDate.of(2024, 5, 1), LocalDate.of(2024, 6, 10)));
        memoryMemberRepository.save(new MemoryMember(member, memory));
        memoryMemberRepository.save(new MemoryMember(member, memory2));
        memoryMemberRepository.save(new MemoryMember(anotherMember, anotherMemberMemory));

        Moment moment = momentRepository.save(MomentFixture.create(memory, LocalDateTime.of(2023, 12, 31, 22, 20)));
        Moment moment1 = momentRepository.save(MomentFixture.create(memory, LocalDateTime.of(2024, 1, 1, 22, 20)));
        Moment moment2 = momentRepository.save(MomentFixture.create(memory, LocalDateTime.of(2024, 1, 1, 22, 21)));
        Moment anotherMoment = momentRepository.save(MomentFixture.create(anotherMemberMemory, LocalDateTime.of(2024, 5, 1, 22, 21)));

        // when
        List<Moment> memberResult = momentRepository.findAllByMemory_MemoryMembers_Member(member);
        List<Moment> anotherMemberResult = momentRepository.findAllByMemory_MemoryMembers_Member(anotherMember);

        // then
        assertAll(
                () -> assertThat(memberResult.size()).isEqualTo(3),
                () -> assertThat(memberResult).containsExactlyInAnyOrder(moment, moment1, moment2),
                () -> assertThat(anotherMemberResult.size()).isEqualTo(1),
                () -> assertThat(anotherMemberResult).containsExactlyInAnyOrder(anotherMoment)
        );
    }
}
