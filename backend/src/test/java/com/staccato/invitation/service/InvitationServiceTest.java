package com.staccato.invitation.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.exception.ForbiddenException;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.domain.InvitationStatus;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.invitation.service.dto.CategoryInvitationRequest;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class InvitationServiceTest extends ServiceSliceTest {
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryInvitationRepository categoryInvitationRepository;

    @DisplayName("HOST가 카테고리에 guest1과 guest2에 초대 요청(REQUESTED)을 한다.")
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
        List<CategoryMember> categoryMembers = categoryRepository.findWithCategoryMembersById(category.getId()).get()
                .getCategoryMembers();
        List<CategoryInvitation> invitations = categoryInvitationRepository.findAllWithCategoryAndMembersByInviterId(host.getId());

        assertAll(
                () -> assertThat(categoryMembers).hasSize(1),
                () -> assertThat(invitations.stream().map(CategoryInvitation::getCategory).collect(Collectors.toSet()))
                        .hasSize(1)
                        .containsExactlyInAnyOrder(category),
                () -> assertThat(invitations.stream().map(CategoryInvitation::getInviter).collect(Collectors.toSet()))
                        .hasSize(1)
                        .containsExactlyInAnyOrder(host),
                () -> assertThat(invitations.stream().map(CategoryInvitation::getInvitee).collect(Collectors.toList()))
                        .hasSize(2)
                        .containsExactlyInAnyOrder(guest1, guest2),
                () -> assertThat(invitations.stream().map(CategoryInvitation::getStatus).collect(Collectors.toSet()))
                        .hasSize(1)
                        .containsExactlyInAnyOrder(InvitationStatus.REQUESTED)
        );
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

    @DisplayName("닉네임 목록 중 일부가 존재하지 않아도 존재하는 멤버만 초대 요청된다.")
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
        List<CategoryInvitation> invitations = categoryInvitationRepository.findAllWithCategoryAndMembersByInviterId(host.getId());

        assertAll(
                () -> assertThat(invitations).hasSize(1),
                () -> assertThat(invitations.get(0).getInvitee()).isEqualTo(guest)
        );
    }
}
