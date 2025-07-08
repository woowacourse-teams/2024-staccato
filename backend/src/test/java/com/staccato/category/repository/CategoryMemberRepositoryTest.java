package com.staccato.category.repository;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import com.staccato.RepositoryTest;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.category.CategoryMemberFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @DisplayName("사용자 식별자와 날짜 값으로 카테고리 목록을 조회한다.")
    @Test
    void findAllByMemberIdAndDate() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryFixtures.defaultCategory()
                .withTerm(null, null)
                .withTitle("no-term")
                .withHost(member)
                .buildAndSave(categoryRepository);
        CategoryFixtures.defaultCategory()
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31))
                .withTitle("term")
                .withHost(member)
                .buildAndSave(categoryRepository);

        // when
        List<CategoryMember> result = categoryMemberRepository.findAllByMemberIdAndDateAndIsShared(
                member.getId(), LocalDate.of(2024, 6, 1), false);
        List<CategoryMember> otherResult = categoryMemberRepository.findAllByMemberIdAndDateAndIsShared(
                member.getId(), LocalDate.of(2023, 6, 1), false);

        // then
        assertAll(
                () -> assertThat(result)
                        .hasSize(2)
                        .extracting(cm -> cm.getCategory().getTitle().getTitle())
                        .containsExactly("no-term", "term"),

                () -> assertThat(otherResult)
                        .hasSize(1)
                        .extracting(cm -> cm.getCategory().getTitle().getTitle())
                        .containsExactly("no-term")
        );
    }


    @DisplayName("사용자 식별자와 공유 flag 값으로 카테고리 목록을 조회한다.")
    @Test
    void findAllByMemberIdAndFlag() {
        // given
        Member host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);
        CategoryFixtures.defaultCategory()
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31))
                .withTitle("shared")
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);
        CategoryFixtures.defaultCategory()
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31))
                .withTitle("nonShared")
                .withHost(host)
                .buildAndSave(categoryRepository);

        // when
        List<CategoryMember> nonSharedResult = categoryMemberRepository.findAllByMemberIdAndDateAndIsShared(
                host.getId(), LocalDate.of(2024, 6, 1), false);
        List<CategoryMember> sharedResult = categoryMemberRepository.findAllByMemberIdAndDateAndIsShared(
                host.getId(), LocalDate.of(2024, 6, 1), true);

        // then
        assertAll(
                () -> assertThat(nonSharedResult)
                        .hasSize(1)
                        .extracting(cm -> cm.getCategory().getTitle().getTitle())
                        .containsExactly("nonShared"),
                () -> assertThat(sharedResult)
                        .hasSize(1)
                        .extracting(cm -> cm.getCategory().getTitle().getTitle())
                        .containsExactly("shared")
        );
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
        List<CategoryMember> result = categoryMemberRepository.findAllByMemberIdAndDateAndIsShared(member.getId(), LocalDate.of(2024, 6, 1), false);

        // then
        assertThat(result).hasSize(2);
    }

    @DisplayName("특정 카테고리의 id를 가지고 있는 모든 CategoryMember를 삭제한다.")
    @Test
    void deleteAllByCategoryIdInBulk() {
        // given
        Member member1 = MemberFixtures.defaultMember().withNickname("member1").buildAndSave(memberRepository);
        Member member2 = MemberFixtures.defaultMember().withNickname("member2").buildAndSave(memberRepository);
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

    @DisplayName("카테고리 멤버는 (카테고리, 멤버) 쌍으로 유일해야 한다.")
    @Test
    void categoryMemberShouldBeUniqueByCategoryAndMember() {
        Category category = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryMember categoryMember1 = CategoryMemberFixtures.defaultCategoryMember()
                .withCategory(category)
                .withMember(member)
                .build();
        CategoryMember categoryMember2 = CategoryMemberFixtures.defaultCategoryMember()
                .withCategory(category)
                .withMember(member)
                .build();

        categoryMemberRepository.save(categoryMember1);
        categoryMemberRepository.flush();

        assertThatThrownBy(() -> {
            categoryMemberRepository.save(categoryMember2);
            categoryMemberRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("특정 카테고리에 속한 모든 멤버를 조회한다.")
    @Test
    void findAllMembersByCategoryId() {
        // given
        Member host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);
        Member guest2 = MemberFixtures.defaultMember().withNickname("guest2").buildAndSave(memberRepository);

        Category category = CategoryFixtures.defaultCategory()
                .withTitle("category")
                .withHost(host)
                .withGuests(List.of(guest, guest2))
                .buildAndSave(categoryRepository);
        CategoryFixtures.defaultCategory()
                .withTitle("anotherCategory")
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);

        // when
        List<Member> result = categoryMemberRepository.findAllMembersByCategoryId(category.getId());

        // then
        assertThat(result)
                .hasSize(3)
                .extracting(member -> member.getNickname().getNickname())
                .containsExactlyInAnyOrder("host", "guest", "guest2");
    }
}
