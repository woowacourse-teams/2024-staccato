package com.staccato.invitation.repository;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryInvitationRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryInvitationRepository categoryInvitationRepository;

    private Member host;
    private Member guest;

    @BeforeEach
    void init() {
        host = MemberFixtures.defaultMember()
                .withNickname("host")
                .buildAndSave(memberRepository);
        guest = MemberFixtures.defaultMember()
                .withNickname("guest")
                .buildAndSave(memberRepository);
    }

    @DisplayName("특정 사용자가 요청(REQUESTED)한 초대 목록을 최신순으로 조회한다.")
    @Test
    void readAllByInviter() {
        // given
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .buildAndSave(categoryRepository);
        Category category2 = CategoryFixtures.defaultCategory()
                .withHost(host)
                .buildAndSave(categoryRepository);
        Category otherCategory = CategoryFixtures.defaultCategory()
                .withHost(guest)
                .buildAndSave(categoryRepository);
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);
        CategoryInvitation invitation2 = CategoryInvitation.invite(category2, host, guest);
        CategoryInvitation otherInvitation = CategoryInvitation.invite(otherCategory, guest, host);
        categoryInvitationRepository.saveAll(List.of(invitation, invitation2, otherInvitation));

        // when
        List<CategoryInvitation> invitations = categoryInvitationRepository.findAllWithCategoryAndInviteeByInviterIdOrderByCreatedAtDesc(host.getId());

        // then
        assertThat(invitations).hasSize(2)
                .containsExactly(invitation2, invitation)
                .doesNotContain(otherInvitation);
    }
}
