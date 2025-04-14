package com.staccato.category.repository;

import com.staccato.category.domain.Category;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.RepositoryTest;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.category.CategoryMemberFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.domain.CategoryMember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CategoryMemberRepositoryTest extends RepositoryTest {
    @Autowired
    private CategoryMemberRepository categoryMemberRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @DisplayName("사용자의 모든 카테고리 목록을 조회한다.")
    @Test
    void findAllByMemberId() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category1 = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        Category category2 = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        CategoryMemberFixtures.defaultCategoryMember()
                .withMember(member)
                .withCategory(category1).buildAndSave(categoryMemberRepository);
        CategoryMemberFixtures.defaultCategoryMember()
                .withMember(member)
                .withCategory(category2).buildAndSave(categoryMemberRepository);

        // when
        List<CategoryMember> result = categoryMemberRepository.findAllByMemberId(member.getId());

        // then
        assertThat(result).hasSize(2);
    }

    @DisplayName("사용자 식별자와 날짜로 카테고리 목록을 조회한다.")
    @Test
    void findAllByMemberIdAndDate() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category1 = CategoryFixtures.defaultCategory()
                .withTerm(LocalDate.of(2023, 1, 1),
                        LocalDate.of(2023, 12, 31))
                .buildAndSave(categoryRepository);
        Category category2 = CategoryFixtures.defaultCategory()
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31))
                .buildAndSave(categoryRepository);
        CategoryMemberFixtures.defaultCategoryMember()
                .withMember(member)
                .withCategory(category1).buildAndSave(categoryMemberRepository);
        CategoryMemberFixtures.defaultCategoryMember()
                .withMember(member)
                .withCategory(category2).buildAndSave(categoryMemberRepository);

        // when
        List<CategoryMember> result = categoryMemberRepository.findAllByMemberIdAndDate(
                member.getId(), LocalDate.of(2024, 6, 1));

        // then
        assertThat(result).hasSize(1);
    }

    @DisplayName("사용자 식별자와 날짜로 카테고리 목록을 조회할 때 카테고리에 기한이 없을 경우 함께 조회한다.")
    @Test
    void findAllByMemberIdAndDateWhenNull() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category1 = CategoryFixtures.defaultCategory()
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31))
                .buildAndSave(categoryRepository);
        Category category2 = CategoryFixtures.defaultCategory()
                .withTerm(null, null)
                .buildAndSave(categoryRepository);
        CategoryMemberFixtures.defaultCategoryMember()
                .withMember(member)
                .withCategory(category1).buildAndSave(categoryMemberRepository);
        CategoryMemberFixtures.defaultCategoryMember()
                .withMember(member)
                .withCategory(category2).buildAndSave(categoryMemberRepository);

        // when
        List<CategoryMember> result = categoryMemberRepository.findAllByMemberIdAndDate(member.getId(), LocalDate.of(2024, 6, 1));

        // then
        assertThat(result).hasSize(2);
    }

    @DisplayName("특정 카테고리의 id를 가지고 있는 모든 CategoryMember를 삭제한다.")
    @Test
    void deleteAllByCategoryIdInBulk() {
        // given
        Member member1 = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member member2 = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        CategoryMember categoryMember1 = CategoryMemberFixtures.defaultCategoryMember()
                .withMember(member1)
                .withCategory(category).buildAndSave(categoryMemberRepository);
        CategoryMember categoryMember2 = CategoryMemberFixtures.defaultCategoryMember()
                .withMember(member2)
                .withCategory(category).buildAndSave(categoryMemberRepository);

        // when
        categoryMemberRepository.deleteAllByCategoryIdInBulk(category.getId());
        entityManager.flush();
        entityManager.clear();

        // then
        assertAll(
                () -> assertThat(categoryMemberRepository.findById(categoryMember1.getId()).isEmpty()).isTrue(),
                () -> assertThat(categoryMemberRepository.findById(categoryMember2.getId()).isEmpty()).isTrue()
        );
    }
}
