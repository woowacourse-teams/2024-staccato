package com.staccato.category.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.RepositoryTest;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;

class CategoryRepositoryTest extends RepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Member host;
    private Member guest;

    @BeforeEach
    void setUp() {
        host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);
    }

    @DisplayName("카테고리 id로 조회 시, categoryMembers와 member까지 함께 조회된다")
    @Test
    void findWithCategoryMembersByIdWithCategoryMembersAndMembers() {
        // given
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);

        // when
        Category result = categoryRepository.findWithCategoryMembersById(category.getId()).get();

        // then
        List<Member> resultMembers = result.getCategoryMembers().stream()
                .map(CategoryMember::getMember)
                .toList();

        assertAll(
                () -> assertThat(result.getCategoryMembers()).hasSize(2),
                () -> assertThat(resultMembers).hasSize(2)
                        .containsExactlyInAnyOrder(host, guest)
        );
    }

    @DisplayName("특정 날짜(specificDate)가 주어지면 해당 날짜를 포함하는 모든 카테고리 목록을 조회한다.")
    @Test
    void findByMemberIdAndDateWithPrivateFlagWhenSpecificDateIsGiven() {
        // given
        Category privateCategoryIn2024 = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31))
                .buildAndSave(categoryRepository);
        Category publicCategoryIn2024 = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest))
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31))
                .buildAndSave(categoryRepository);
        Category privateCategoryIn2025 = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withTerm(LocalDate.of(2025, 1, 1),
                        LocalDate.of(2025, 12, 31))
                .buildAndSave(categoryRepository);
        Category publicCategoryIn2025 = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest))
                .withTerm(LocalDate.of(2025, 1, 1),
                        LocalDate.of(2025, 12, 31))
                .buildAndSave(categoryRepository);

        // when
        LocalDate specificDateIn2024 = LocalDate.of(2024, 6, 1);
        Boolean defaultIsShared = null;
        List<Category> categories = categoryRepository.findAllByMemberIdAndDateAndSharingFilter(host.getId(), specificDateIn2024, defaultIsShared);

        // then
        assertThat(categories).hasSize(2)
                .containsExactlyInAnyOrder(privateCategoryIn2024, publicCategoryIn2024)
                .doesNotContain(privateCategoryIn2025, publicCategoryIn2025);
    }

    @DisplayName("공유카테고리여부(isShared)가 null이면 해당 멤버의 전체 카테고리(개인/공동) 목록을 조회한다.")
    @Test
    void findAllByMemberIdAndDateAndSharingFilterIsNull() {
        // given
        Category privateCategory = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withTerm(null, null)
                .buildAndSave(categoryRepository);
        Category publicCategory = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest))
                .withTerm(null, null)
                .buildAndSave(categoryRepository);

        // when
        List<Category> categories = categoryRepository.findAllByMemberIdAndDateAndSharingFilter(host.getId(), LocalDate.now(), null);

        // then
        assertThat(categories).hasSize(2)
                .containsExactlyInAnyOrder(privateCategory, publicCategory);
    }

    @DisplayName("공유카테고리여부(isShared)가 false이면 해당 멤버의 개인 카테고리 목록만 조회한다.")
    @Test
    void findAllByMemberIdAndDateAndSharingFilterIsFalse() {
        // given
        Category privateCategory = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withTerm(null, null)
                .buildAndSave(categoryRepository);
        Category publicCategory = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest))
                .withTerm(null, null)
                .buildAndSave(categoryRepository);

        // when
        List<Category> categories = categoryRepository.findAllByMemberIdAndDateAndSharingFilter(host.getId(), LocalDate.now(), false);

        // then
        assertThat(categories).hasSize(1)
                .containsExactly(privateCategory)
                .doesNotContain(publicCategory);
    }
}
