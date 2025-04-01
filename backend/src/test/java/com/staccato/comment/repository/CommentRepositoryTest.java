package com.staccato.comment.repository;

import com.staccato.category.domain.Category;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.comment.CommentFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.staccato.domain.Staccato;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
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
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        Staccato staccato1 = StaccatoFixtures.defaultStaccato()
                .withCategory(category).build();
        CommentFixtures.defaultComment()
                .withStaccato(staccato1)
                .withMember(member).build();
        Staccato staccato2 = StaccatoFixtures.defaultStaccato()
                .withCategory(category).build();
        CommentFixtures.defaultComment()
                .withStaccato(staccato2)
                .withMember(member).build();
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
