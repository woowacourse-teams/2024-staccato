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
    private Category privateCategory;
    private Category publicCategory;

    @BeforeEach
    void setUp() {
        host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);
        privateCategory = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withTerm(null, null)
                .buildAndSave(categoryRepository);
        publicCategory = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest))
                .withTerm(null, null)
                .buildAndSave(categoryRepository);
    }


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

    @DisplayName("개인카테고리여부(isPrivate)가 false이면 해당 멤버의 전체 카테고리(개인/공동) 목록을 조회한다.")
    @Test
    void findByMemberIdAndDateWithPrivateFlagWhenPrivateFlagIsFalse() {
        // given & when
        List<Category> categories = categoryRepository.findByMemberIdAndDateWithPrivateFlag(host.getId(), LocalDate.now(), false);

        // then
        assertThat(categories).hasSize(2)
                .extracting(Category::getId)
                .containsExactlyInAnyOrder(privateCategory.getId(), publicCategory.getId());
    }

    @DisplayName("개인카테고리여부(isPrivate)가 true이면 해당 멤버의 개인 카테고리 목록만 조회한다.")
    @Test
    void findByMemberIdAndDateWithPrivateFlagWhenPrivateFlagIsTrue() {
        // given & when
        List<Category> categories = categoryRepository.findByMemberIdAndDateWithPrivateFlag(host.getId(), LocalDate.now(), true);

        // then
        assertThat(categories).hasSize(1)
                .extracting(Category::getId)
                .containsExactly(privateCategory.getId());
    }
}
