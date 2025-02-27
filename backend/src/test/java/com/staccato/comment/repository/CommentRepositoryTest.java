package com.staccato.comment.repository;

import com.staccato.category.domain.Category;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.comment.CommentFixture;
import com.staccato.fixture.category.CategoryFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.repository.MomentRepository;

import static org.assertj.core.api.Assertions.assertThat;

class CommentRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MomentRepository momentRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CommentRepository commentRepository;
    @PersistenceContext
    private EntityManager em;

    @DisplayName("특정 스타카토의 id를 여러개를 가지고 있는 모든 댓글들을 삭제한다.")
    @Test
    void deleteAllByMomentIdInBulk() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Category category = categoryRepository.save(CategoryFixture.create(null, null));
        Moment moment1 = MomentFixture.create(category);
        CommentFixture.create(moment1, member);
        Moment moment2 = MomentFixture.create(category);
        CommentFixture.create(moment2, member);
        momentRepository.save(moment1);
        momentRepository.save(moment2);

        // when
        commentRepository.deleteAllByMomentIdInBulk(List.of(moment1.getId(), moment2.getId()));
        em.flush();
        em.clear();

        // then
        assertThat(commentRepository.findAll()).isEqualTo(List.of());
    }
}
