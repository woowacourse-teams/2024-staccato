package com.staccato.comment.repository;

import com.staccato.category.domain.Category;
import com.staccato.staccato.domain.Staccato;
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
import com.staccato.fixture.moment.StaccatoFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.staccato.repository.StaccatoRepository;

import static org.assertj.core.api.Assertions.assertThat;

class CommentRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StaccatoRepository staccatoRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CommentRepository commentRepository;
    @PersistenceContext
    private EntityManager em;

    @DisplayName("특정 스타카토의 id를 여러개를 가지고 있는 모든 댓글들을 삭제한다.")
    @Test
    void deleteAllByStaccatoIdInBulk() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Category category = categoryRepository.save(CategoryFixture.create(null, null));
        Staccato staccato1 = StaccatoFixture.create(category);
        CommentFixture.create(staccato1, member);
        Staccato staccato2 = StaccatoFixture.create(category);
        CommentFixture.create(staccato2, member);
        staccatoRepository.save(staccato1);
        staccatoRepository.save(staccato2);

        // when
        commentRepository.deleteAllByStaccatoIdInBulk(List.of(staccato1.getId(), staccato2.getId()));
        em.flush();
        em.clear();

        // then
        assertThat(commentRepository.findAll()).isEqualTo(List.of());
    }
}
