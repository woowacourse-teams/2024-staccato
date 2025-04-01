package com.staccato.staccato.repository;

import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.staccato.domain.Staccato;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.domain.CategoryMember;
import com.staccato.category.repository.CategoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class StaccatoRepositoryTest extends RepositoryTest {
    @Autowired
    private StaccatoRepository staccatoRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMemberRepository categoryMemberRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @DisplayName("사용자의 모든 스타카토를 조회한다.")
    @Test
    void findAllByCategory_CategoryMembers_Member() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member anotherMember = MemberFixtures.defaultMember()
                .withNickname("anotherMember")
                .buildAndSave(memberRepository);
        Category category1 = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        Category category2 = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        Category anotherMemberCategory = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        categoryMemberRepository.save(new CategoryMember(member, category1));
        categoryMemberRepository.save(new CategoryMember(member, category2));
        categoryMemberRepository.save(new CategoryMember(anotherMember, anotherMemberCategory));

        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category1).buildAndSave(staccatoRepository);
        Staccato staccato1 = StaccatoFixtures.defaultStaccato()
                .withCategory(category1).buildAndSave(staccatoRepository);
        Staccato staccato2 = StaccatoFixtures.defaultStaccato()
                .withCategory(category1).buildAndSave(staccatoRepository);
        Staccato anotherStaccato = StaccatoFixtures.defaultStaccato()
                .withCategory(anotherMemberCategory).buildAndSave(staccatoRepository);

        // when
        List<Staccato> memberResult = staccatoRepository.findAllByCategory_CategoryMembers_Member(member);
        List<Staccato> anotherMemberResult = staccatoRepository.findAllByCategory_CategoryMembers_Member(anotherMember);

        // then
        assertAll(
                () -> assertThat(memberResult.size()).isEqualTo(3),
                () -> assertThat(memberResult).containsExactlyInAnyOrder(staccato, staccato1,
                    staccato2),
                () -> assertThat(anotherMemberResult.size()).isEqualTo(1),
                () -> assertThat(anotherMemberResult).containsExactlyInAnyOrder(anotherStaccato)
        );
    }

    @DisplayName("특정 카테고리의 id를 가진 모든 스타카토를 삭제한다.")
    @Test
    void deleteAllByCategoryIdInBulk() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        categoryMemberRepository.save(new CategoryMember(member, category));

        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        Staccato staccato1 = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        Staccato staccato2 = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        // when
        staccatoRepository.deleteAllByCategoryIdInBulk(category.getId());
        entityManager.flush();
        entityManager.clear();

        // then
        assertAll(
                () -> assertThat(staccatoRepository.findById(staccato.getId()).isEmpty()).isTrue(),
                () -> assertThat(staccatoRepository.findById(staccato1.getId()).isEmpty()).isTrue(),
                () -> assertThat(staccatoRepository.findById(staccato2.getId()).isEmpty()).isTrue(),
                () -> assertThat(staccatoRepository.findAllByCategoryId(category.getId())).isEqualTo(List.of())
        );
    }

    @DisplayName("사용자의 특정 카테고리에 해당하는 모든 스타카토를 최신순으로 조회한다.")
    @Test
    void findAllByCategoryIdOrderByVisitedAt() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        categoryMemberRepository.save(new CategoryMember(member, category));

        Staccato staccato1 = StaccatoFixtures.defaultStaccato()
                .withVisitedAt(LocalDateTime.of(2024, 6, 1, 0, 0))
                .withCategory(category).buildAndSave(staccatoRepository);
        Staccato staccato2 = StaccatoFixtures.defaultStaccato()
                .withVisitedAt(LocalDateTime.of(2024, 6, 2, 0, 0))
                .withCategory(category).buildAndSave(staccatoRepository);
        Staccato staccato3 = StaccatoFixtures.defaultStaccato()
                .withVisitedAt(LocalDateTime.of(2024, 6, 3, 0, 0))
                .withCategory(category).buildAndSave(staccatoRepository);

        // when
        List<Staccato> staccatos = staccatoRepository.findAllByCategoryIdOrdered(category.getId());

        // then
        assertAll(
                () -> assertThat(staccatos.size()).isEqualTo(3),
                () -> assertThat(staccatos).containsExactly(staccato3, staccato2, staccato1)
        );
    }

    @DisplayName("사용자의 스타카토 방문 날짜가 동일하다면, 생성날짜 기준 최신순으로 조회한다.")
    @Test
    void findAllByCategoryIdOrderByCreatedAt() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        categoryMemberRepository.save(new CategoryMember(member, category));

        Staccato staccato1 = StaccatoFixtures.defaultStaccato()
                .withVisitedAt(LocalDateTime.of(2024, 6, 1, 0, 0))
                .withCategory(category).buildAndSave(staccatoRepository);
        Staccato staccato2 = StaccatoFixtures.defaultStaccato()
                .withVisitedAt(LocalDateTime.of(2024, 6, 1, 0, 0))
                .withCategory(category).buildAndSave(staccatoRepository);

        // when
        List<Staccato> staccatos = staccatoRepository.findAllByCategoryIdOrdered(category.getId());

        // then
        assertAll(
                () -> assertThat(staccatos.size()).isEqualTo(2),
                () -> assertThat(staccatos).containsExactly(staccato2, staccato1)
        );
    }
}
