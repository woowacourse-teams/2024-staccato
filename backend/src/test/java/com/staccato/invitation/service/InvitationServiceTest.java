package com.staccato.invitation.service;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.exception.ForbiddenException;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.invitation.service.dto.CategoryInvitationRequest;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvitationServiceTest extends ServiceSliceTest {
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @DisplayName("HOST가 닉네임 목록을 통해 카테고리에 멤버를 초대한다.")
    @Test
    void inviteMembers() {
        // given
        Member host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        Member guest1 = MemberFixtures.defaultMember().withNickname("guest1").buildAndSave(memberRepository);
        Member guest2 = MemberFixtures.defaultMember().withNickname("guest2").buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .buildAndSave(categoryRepository);

        CategoryInvitationRequest invitationRequest = new CategoryInvitationRequest(category.getId(), Set.of(guest1.getId(), guest2.getId()));

        // when
        invitationService.inviteMembers(host, invitationRequest);

        // then
        Category savedCategory = categoryRepository.findWithCategoryMembersById(category.getId()).get();

        assertThat(savedCategory.getCategoryMembers()).hasSize(3);
    }

    @DisplayName("특정 카테고리의 HOST가 아닌 멤버가 카테고리 초대 요청을 보내면 예외가 발생한다.")
    @Test
    void failToInviteIfNotHost() {
        // given
        Member host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);
        Member anotherUser = MemberFixtures.defaultMember().withNickname("anotherUser").buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .buildAndSave(categoryRepository);

        CategoryInvitationRequest invitationRequest = new CategoryInvitationRequest(category.getId(), Set.of(guest.getId()));

        // when & then
        assertThatThrownBy(() -> invitationService.inviteMembers(anotherUser, invitationRequest))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("닉네임 목록 중 일부가 존재하지 않아도 존재하는 멤버만 초대된다.")
    @Test
    void inviteOnlyExistingMembers() {
        // given
        Member host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);
        long unknownId = 0;
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .buildAndSave(categoryRepository);

        CategoryInvitationRequest invitationRequest = new CategoryInvitationRequest(category.getId(), Set.of(guest.getId(), unknownId));

        // when
        invitationService.inviteMembers(host, invitationRequest);

        // then
        Category result = categoryRepository.findWithCategoryMembersById(category.getId()).get();

        assertThat(result.getCategoryMembers()).hasSize(2);
    }
}
