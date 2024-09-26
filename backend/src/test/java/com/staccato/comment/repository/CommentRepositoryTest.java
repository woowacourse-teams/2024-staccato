package com.staccato.comment.repository;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.repository.MomentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MomentRepository momentRepository;
    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private CommentRepository commentRepository;
    @PersistenceContext
    private EntityManager em;

    @DisplayName("특정 스타카토의 id를 가지고 있는 모든 댓글들을 삭제한다.")
    @Test
    void deleteAllByMomentIdInBatch() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Memory memory = memoryRepository.save(MemoryFixture.create(null, null));
        Moment moment = MomentFixture.createWithComments(member, memory, List.of("content1", "content2", "content3"));
        momentRepository.save(moment);

        // when
        commentRepository.deleteAllByMomentIdInBatch(moment.getId());
        em.flush();
        em.clear();

        // then
        assertAll(
                () -> assertThat(commentRepository.findAll()).isEqualTo(List.of()),
                () -> assertThat(momentRepository.findById(moment.getId()).get().getComments().size()).isEqualTo(0)
        );
    }
}
