package com.staccato.comment.repository;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.comment.CommentFixture;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.repository.MomentRepository;

import static org.assertj.core.api.Assertions.assertThat;

class CommentRepositoryTest extends RepositoryTest {
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

    @DisplayName("특정 스타카토의 id를 여러개를 가지고 있는 모든 댓글들을 삭제한다.")
    @Test
    void deleteAllByMomentIdInBatch() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Memory memory = memoryRepository.save(MemoryFixture.create(null, null));
        Moment moment1 = MomentFixture.create(memory);
        CommentFixture.create(moment1, member);
        Moment moment2 = MomentFixture.create(memory);
        CommentFixture.create(moment2, member);
        momentRepository.save(moment1);
        momentRepository.save(moment2);

        // when
        commentRepository.deleteAllByMomentIdInBatch(List.of(moment1.getId(), moment2.getId()));
        em.flush();
        em.clear();

        // then
        assertThat(commentRepository.findAll()).isEqualTo(List.of());
    }
}
