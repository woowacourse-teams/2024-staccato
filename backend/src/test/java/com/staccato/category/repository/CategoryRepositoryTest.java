package com.staccato.category.repository;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CategoryRepositoryTest extends RepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("카테고리 id로 조회 시, categoryMembers와 member까지 함께 조회된다")
    @Test
    void findWithCategoryMembersByIdWithCategoryMembersAndMembers() {
        // given
        Member member1 = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        Member member2 = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);

        Category category = CategoryFixtures.defaultCategory()
                .withHost(member1)
                .withGuests(List.of(member2))
                .buildAndSave(categoryRepository);

        // when
        Category result = categoryRepository.findWithCategoryMembersById(category.getId()).get();

        // then
        assertAll(
                () -> assertThat(result.getCategoryMembers()).hasSize(2),
                () -> assertThat(result.getCategoryMembers().stream().map(CategoryMember::getMember).toList())
                        .containsExactlyInAnyOrder(member1, member2)
        );
    }

    @Nested
    @DisplayName("사용자 ID의 특정 날짜를 포함하는 카테고리를 조회한다.")
    class findCategoriesByMemberIdAndDate {

        private Member host;
        private Member guest;
        private Category sharedCategoryIn2023;
        private Category sharedCategoryIn2024;
        private Category privateCategoryIn2023;
        private Category privateCategoryIn2024;

        @BeforeEach
        void setUp() {
            host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
            guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);
            sharedCategoryIn2023 = CategoryFixtures.defaultCategory()
                    .withHost(host)
                    .withGuests(List.of(guest))
                    .withTerm(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31))
                    .buildAndSave(categoryRepository);
            sharedCategoryIn2024 = CategoryFixtures.defaultCategory()
                    .withHost(host)
                    .withGuests(List.of(guest))
                    .withTerm(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31))
                    .buildAndSave(categoryRepository);
            privateCategoryIn2023 = CategoryFixtures.defaultCategory()
                    .withHost(host)
                    .withTerm(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31))
                    .buildAndSave(categoryRepository);
            privateCategoryIn2024 = CategoryFixtures.defaultCategory()
                    .withHost(host)
                    .withTerm(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31))
                    .buildAndSave(categoryRepository);
        }

        @DisplayName("사용자 ID의 특정 날짜를 포함하는 모든 카테고리를 조회한다.")
        @Test
        void findAllByMemberIdAndDate() {
            // given & when
            List<Category> categories = categoryRepository.findAllByMemberIdAndDate(host.getId(), LocalDate.of(2023, 6, 1));

            // then
            assertThat(categories)
                    .containsExactlyInAnyOrder(sharedCategoryIn2023, privateCategoryIn2023)
                    .doesNotContain(sharedCategoryIn2024, privateCategoryIn2024);
        }

        @DisplayName("사용자 ID의 특정 날짜를 포함하는 개인 카테고리를 조회한다.")
        @Test
        void findPrivateByMemberIdAndDate() {
            // given & when
            List<Category> categories = categoryRepository.findPrivateByMemberIdAndDate(host.getId(), LocalDate.of(2023, 6, 1));

            // then
            assertThat(categories)
                    .containsExactlyInAnyOrder(privateCategoryIn2023)
                    .doesNotContain(sharedCategoryIn2023, sharedCategoryIn2024, privateCategoryIn2024);
        }
    }
}
