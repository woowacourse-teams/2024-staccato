package com.staccato.category.repository;

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
                .withGuests(member2)
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
}
