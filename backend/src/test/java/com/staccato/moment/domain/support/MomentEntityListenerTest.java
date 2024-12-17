package com.staccato.moment.domain.support;

import java.time.LocalDateTime;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.domain.Feeling;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.repository.MomentRepository;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MomentEntityListenerTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private MomentRepository momentRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @DisplayName("Moment 생성 시 Memory의 updatedAt이 갱신된다.")
    @Test
    void updateMemoryUpdatedDateWhenMomentCreated() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Memory memory = memoryRepository.save(MemoryFixture.createWithMember(member));
        LocalDateTime beforeCreate = memory.getUpdatedAt();

        // when
        momentRepository.save(MomentFixture.create(memory));
        entityManager.flush();
        entityManager.refresh(memory);
        LocalDateTime afterCreate = memory.getUpdatedAt();

        // then
        assertThat(afterCreate).isAfter(beforeCreate);
    }

    @DisplayName("Moment 수정 시 Memory의 updatedAt이 갱신된다.")
    @Test
    void updateMemoryUpdatedDateWhenMomentUpdated() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Memory memory = memoryRepository.save(MemoryFixture.createWithMember(member));
        Moment moment = momentRepository.save(MomentFixture.create(memory));
        LocalDateTime beforeUpdate = memory.getUpdatedAt();

        // when
        moment.changeFeeling(Feeling.ANGRY);
        entityManager.flush();
        entityManager.refresh(memory);
        LocalDateTime afterUpdate = memory.getUpdatedAt();

        // then
        assertThat(afterUpdate).isAfter(beforeUpdate);
    }

    @DisplayName("Moment 삭제 시 Memory의 updatedAt이 갱신된다.")
    @Test
    void updateMemoryUpdatedDateWhenMomentDeleted() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Memory memory = memoryRepository.save(MemoryFixture.createWithMember(member));
        Moment moment = momentRepository.save(MomentFixture.create(memory));
        LocalDateTime beforeDelete = memory.getUpdatedAt();

        // when
        momentRepository.delete(moment);
        entityManager.flush();
        entityManager.refresh(memory);
        LocalDateTime afterDelete = memory.getUpdatedAt();

        // then
        assertThat(afterDelete).isAfter(beforeDelete);
    }
}
